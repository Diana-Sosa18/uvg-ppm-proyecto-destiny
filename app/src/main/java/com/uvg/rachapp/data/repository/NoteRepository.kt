package com.uvg.rachapp.data.repository

import com.uvg.rachapp.data.local.db.daos.NoteDao
import com.uvg.rachapp.data.local.db.entities.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class NotePreview(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
)

private fun NoteEntity.toPreview() = NotePreview(id = id, title = title, content = content, timestamp = timestamp)
private fun NotePreview.toEntity() = NoteEntity(id = if (id == 0) 0 else id, title = title, content = content, timestamp = timestamp)

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<NotePreview>> =
        noteDao.getAllNotes().map { list -> list.map { it.toPreview() } }

    suspend fun insertNote(note: NotePreview) {
        noteDao.insertNote(note.toEntity())
    }

    suspend fun deleteNoteById(id: Int) {
        noteDao.deleteNoteById(id)
    }

    fun getNoteById(id: Int) = noteDao.getNoteById(id).map { it?.toPreview() }

    suspend fun existsByTimestamp(timestamp: Long): Boolean {
        return noteDao.getNoteByTimestamp(timestamp) != null
    }


    suspend fun insertNoteFromCloud(title: String, content: String, timestamp: Long) {
        val preview = NotePreview(id = 0, title = title, content = content, timestamp = timestamp)
        insertNote(preview)
    }

    suspend fun clearAllNotes() {
        noteDao.deleteAllNotes()
    }
}