package com.uvg.rachapp.ui.viewModels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.rachapp.data.repository.NotePreview
import com.uvg.rachapp.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: NoteRepository
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

    fun onTitleChange(new: String) { titleInput = new }
    fun onContentChange(new: String) { contentInput = new }

    fun openAddNoteForm() { showAddNoteForm = true }
    fun closeAddNoteForm() {
        titleInput = ""
        contentInput = ""
        showAddNoteForm = false
    }

    fun addNoteFromInputs() {
        val n = NotePreview(id = 0, title = titleInput.trim(), content = contentInput.trim(), timestamp = System.currentTimeMillis())
        viewModelScope.launch {
            repository.insertNote(n)
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
        if (selectedNote != null) {
            showNoteDetailDialog = true
        }
    }

    fun dismissNoteDetail() {
        showNoteDetailDialog = false
        selectedNote = null
    }

    fun deleteNote(note: NotePreview) {
        viewModelScope.launch {
            repository.deleteNoteById(note.id)
        }
        showNoteDetailDialog = false
        selectedNote = null
    }
}