package com.uvg.rachapp.ui.viewModels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uvg.rachapp.data.repository.FirestoreRepository
import com.uvg.rachapp.data.repository.NotePreview
import com.uvg.rachapp.data.repository.NoteRepository
import com.uvg.rachapp.util.hashPin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val firestoreRepository: FirestoreRepository,
    private val auth: FirebaseAuth

) : ViewModel() {


    val notesState = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    var titleInput by mutableStateOf("")
        private set

    var contentInput by mutableStateOf("")
        private set

    var showAddNoteForm by mutableStateOf(false)
        private set

    var showConfirmDialog by mutableStateOf(false)
        private set

    var showNoteDetailDialog by mutableStateOf(false)
        private set

    var selectedNote by mutableStateOf<NotePreview?>(null)
        private set

    var showCreatePinDialog by mutableStateOf(false)
        private set

    var showEnterPinDialog by mutableStateOf(false)
        private set

    var pinErrorMessage by mutableStateOf<String?>(null)
        private set

    var cachedPinHash: String? = null

    // contador exposable
    var noteCount by mutableStateOf(0L)
        private set

    // ---- UI handlers existentes ----
    fun onTitleChange(new: String) { titleInput = new }
    fun onContentChange(new: String) { contentInput = new }

    fun openAddNoteForm() { showAddNoteForm = true }
    fun closeAddNoteForm() {
        titleInput = ""
        contentInput = ""
        showAddNoteForm = false
    }

    // ---- Integración: añadir nota con sincronización cloud ----
    // Conserva el mismo nombre que usa tu UI: addNoteFromInputs()
    fun addNoteFromInputs() {
        val title = titleInput.trim()
        val content = contentInput.trim()
        if (title.isBlank() && content.isBlank()) {
            // no crear nota vacía
            titleInput = ""
            contentInput = ""
            showAddNoteForm = false
            return
        }
        val ts = System.currentTimeMillis()
        val preview = NotePreview(id = 0, title = title, content = content, timestamp = ts)

        viewModelScope.launch {
            // 1) insertar localmente (Room)
            repository.insertNote(preview)

            // 2) intentar crear en cloud (si falla, dejamos la nota local)
            val user = auth.currentUser
            if (user != null) {
                try {
                    firestoreRepository.createCloudNote(user.uid, mapOf(
                        "title" to title,
                        "content" to content,
                        "timestamp" to ts
                    ))
                    // firestoreRepository.createCloudNote ya incrementa noteCount internamente
                    noteCount += 1
                } catch (e: Exception) {
                    // opcional: marcar para reintento. Por ahora no bloqueamos la UX
                }
            }
        }

        // limpiar UI
        titleInput = ""
        contentInput = ""
        showAddNoteForm = false
    }

    // ---- selección / mostrar nota ----
    // cuando el usuario toca la nota: abrimos diálogo para pedir PIN primero
    fun selectNoteForConfirm(note: NotePreview) {
        selectedNote = note
        // en lugar de abrir confirm directo, abrimos confirm que pedirá PIN
        showConfirmDialog = true
    }

    fun dismissConfirm() {
        showConfirmDialog = false
        selectedNote = null
    }

    // cuando confirman ver (en tu ConfirmViewDialog), ahora mostramos diálogo para ingresar PIN
    fun confirmViewSelectedNote() {
        showConfirmDialog = false
        // abrir el diálogo de ingreso de PIN
        showEnterPinDialog = true
    }

    // llamada desde el diálogo de ingreso de PIN en la UI
    fun enterPinAndShow(enteredPin: String) {
        if (verifyPin(enteredPin)) {
            // PIN correcto: mostrar detalle
            showEnterPinDialog = false
            showNoteDetailDialog = true
            pinErrorMessage = null
        } else {
            pinErrorMessage = "PIN incorrecto"
        }
    }

    fun dismissNoteDetail() {
        showNoteDetailDialog = false
        selectedNote = null
    }

    // ---- borrar nota con sincronización ----
    // conservamos la firma usada en la UI: deleteNote(note)
    fun deleteNote(note: NotePreview) {
        val user = auth.currentUser
        viewModelScope.launch {
            // 1) borrar local
            repository.deleteNoteById(note.id)

            // 2) intentar borrar en cloud por timestamp (no cambiamos schema)
            if (user != null) {
                try {
                    firestoreRepository.deleteCloudNoteByTimestamp(user.uid, note.timestamp)
                    // deleteCloudNoteByTimestamp decrementa contador internamente, si implementaste así
                    noteCount = (noteCount - 1).coerceAtLeast(0)
                } catch (e: Exception) {
                    // manejar fallo: reintentar más tarde
                }
            }
        }
        showNoteDetailDialog = false
        selectedNote = null
    }

    // ---- PIN management ----
    // Llamar desde JournalScreen (LaunchedEffect) al abrir la pantalla
    fun checkOrCreatePinOnFirstOpen() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val pinHash = firestoreRepository.getUserPinHash(user.uid)
                if (pinHash == null) {
                    showCreatePinDialog = true
                } else {
                    cachedPinHash = pinHash
                }

                // también obtener contador inicial
                noteCount = firestoreRepository.getNoteCount(user.uid)

                // sincronizar notas cloud -> local (dedupe por timestamp)
                val cloudNotes = firestoreRepository.getAllCloudNotes(user.uid)
                for (m in cloudNotes) {
                    val ts = (m["timestamp"] as? Number)?.toLong() ?: continue
                    val exists = repository.existsByTimestamp(ts)
                    if (!exists) {
                        val t = m["title"] as? String ?: ""
                        val c = m["content"] as? String ?: ""
                        repository.insertNoteFromCloud(t, c, ts)
                    }
                }
            } catch (e: Exception) {
                // manejar/log (no bloquear UI)
            }
        }
    }

    fun saveNewPin(plainPin: String) {
        val user = auth.currentUser ?: return
        val hash = hashPin(plainPin)
        viewModelScope.launch {
            try {
                firestoreRepository.saveUserPinHash(user.uid, hash)
                cachedPinHash = hash
                showCreatePinDialog = false
                pinErrorMessage = null
            } catch (e: Exception) {
                pinErrorMessage = "No se pudo guardar el PIN"
            }
        }
    }

    fun verifyPin(enteredPin: String): Boolean {
        val enteredHash = hashPin(enteredPin)
        val ok = enteredHash == cachedPinHash
        if (!ok) pinErrorMessage = "PIN incorrecto"
        return ok
    }

    fun dismissCreatePinDialog() {
        showCreatePinDialog = false
        pinErrorMessage = null
    }

    fun dismissEnterPinDialog() {
        showEnterPinDialog = false
        pinErrorMessage = null
    }


}