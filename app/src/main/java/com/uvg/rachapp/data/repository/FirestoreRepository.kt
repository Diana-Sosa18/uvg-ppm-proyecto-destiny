package com.uvg.rachapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private fun userDocRef(uid: String) = firestore.collection("users").document(uid)
    private fun userNotesCollection(uid: String) = userDocRef(uid).collection("notes")

    suspend fun getUserPinHash(uid: String): String? {
        val snap = userDocRef(uid).get().await()
        return if (snap.exists()) snap.getString("pinHash") else null
    }

    suspend fun saveUserPinHash(uid: String, pinHash: String) {
        userDocRef(uid).set(mapOf("pinHash" to pinHash), com.google.firebase.firestore.SetOptions.merge()).await()
    }

    suspend fun incrementNoteCount(uid: String, delta: Long) {
        userDocRef(uid).update("noteCount", FieldValue.increment(delta)).await()
    }

    suspend fun getNoteCount(uid: String): Long {
        val snap = userDocRef(uid).get().await()
        return snap.getLong("noteCount") ?: 0L
    }

    // Crear nota en cloud (devuelve doc id)
    suspend fun createCloudNote(uid: String, noteMap: Map<String, Any>): String {
        val docRef = userNotesCollection(uid).add(noteMap).await()
        // incrementa contador
        incrementNoteCount(uid, 1L)
        return docRef.id
    }

    // Obtener notas en cloud (one-shot)
    suspend fun getAllCloudNotes(uid: String): List<Map<String, Any>> {
        val snaps = userNotesCollection(uid).get().await()
        return snaps.documents.map { d ->
            val m = d.data?.toMutableMap() ?: mutableMapOf()
            m["cloudId"] = d.id
            m
        }
    }

    // Borrar nota cloud por timestamp (buscamos por timestamp y borramos coincidencias)
    suspend fun deleteCloudNoteByTimestamp(uid: String, timestamp: Long) {
        val snaps = userNotesCollection(uid).whereEqualTo("timestamp", timestamp).get().await()
        for (doc in snaps.documents) {
            userNotesCollection(uid).document(doc.id).delete().await()
            incrementNoteCount(uid, -1L)
        }
    }
}