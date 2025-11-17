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
import kotlinx.coroutines.tasks.await
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


    var noteCount by mutableStateOf(0L)
        private set


    fun onTitleChange(new: String) { titleInput = new }
    fun onContentChange(new: String) { contentInput = new }

    fun openAddNoteForm() { showAddNoteForm = true }
    fun closeAddNoteForm() {
        titleInput = ""
        contentInput = ""
        showAddNoteForm = false
    }


    fun addNoteFromInputs() {
        val title = titleInput.trim()
        val content = contentInput.trim()
        if (title.isBlank() && content.isBlank()) {

            titleInput = ""
            contentInput = ""
            showAddNoteForm = false
            return
        }
        val ts = System.currentTimeMillis()
        val preview = NotePreview(id = 0, title = title, content = content, timestamp = ts)

        viewModelScope.launch {

            repository.insertNote(preview)


            val user = auth.currentUser
            if (user != null) {
                try {
                    firestoreRepository.createCloudNote(user.uid, mapOf(
                        "title" to title,
                        "content" to content,
                        "timestamp" to ts
                    ))

                    noteCount += 1
                } catch (e: Exception) {

                }
            }
        }


        titleInput = ""
        contentInput = ""
        showAddNoteForm = false
    }



    fun selectNoteForConfirm(note: NotePreview) {
        selectedNote = note

        showConfirmDialog = true
    }

    fun dismissConfirm() {
        showConfirmDialog = false
        selectedNote = null
    }


    fun confirmViewSelectedNote() {
        showConfirmDialog = false

        showEnterPinDialog = true
    }


    fun enterPinAndShow(enteredPin: String) {
        if (verifyPin(enteredPin)) {

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


    fun deleteNote(note: NotePreview) {
        val user = auth.currentUser
        viewModelScope.launch {

            try {
                repository.deleteNoteById(note.id)
                android.util.Log.d("SYNC", "Deleted local note id=${note.id} ts=${note.timestamp}")
            } catch (e: Exception) {
                android.util.Log.e("SYNC", "Error deleting local note id=${note.id}", e)
            }


            if (user != null) {
                try {
                    android.util.Log.d("SYNC", "Attempting cloud delete for uid=${user.uid} ts=${note.timestamp}")
                    android.util.Log.d("SYNC", "Before cloud delete: auth.currentUser?.uid=${auth.currentUser?.uid}, expected uid=${user?.uid}, note.ts=${note.timestamp}")
                    firestoreRepository.deleteCloudNoteByTimestamp(user.uid, note.timestamp)

                    noteCount = (noteCount - 1).coerceAtLeast(0)
                    android.util.Log.d("SYNC", "Cloud delete attempted for ts=${note.timestamp} uid=${user.uid}")
                } catch (e: Exception) {
                    android.util.Log.e("SYNC", "Error deleting cloud note for ts=${note.timestamp} uid=${user.uid}", e)

                }


                try {
                    val snaps = firestoreRepository.userNotesCollection(user.uid).get().await()
                    android.util.Log.d("SYNC", "Post-delete cloud snapshot size = ${snaps.size()} for uid=${user.uid}")
                } catch (e: Exception) {
                    android.util.Log.e("SYNC", "Error fetching cloud notes after delete for uid=${user.uid}", e)
                }
            } else {
                android.util.Log.w("SYNC", "No user logged in; skipped cloud delete for ts=${note.timestamp}")
            }
        }


        showNoteDetailDialog = false
        selectedNote = null
    }


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


                noteCount = firestoreRepository.getNoteCount(user.uid)


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