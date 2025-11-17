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
    fun userNotesCollection(uid: String) = userDocRef(uid).collection("notes")

    suspend fun getUserPinHash(uid: String): String? {
        val snap = userDocRef(uid).get().await()
        return if (snap.exists()) snap.getString("pinHash") else null
    }

    suspend fun saveUserPinHash(uid: String, pinHash: String) {
        userDocRef(uid).set(
            mapOf("pinHash" to pinHash),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
    }

    suspend fun incrementNoteCount(uid: String, delta: Long) {
        userDocRef(uid).update("noteCount", FieldValue.increment(delta)).await()
    }

    suspend fun getNoteCount(uid: String): Long {
        val snap = userDocRef(uid).get().await()
        return snap.getLong("noteCount") ?: 0L
    }


    suspend fun createCloudNote(uid: String, noteMap: Map<String, Any>): String {
        val docRef = userNotesCollection(uid).add(noteMap).await()

        incrementNoteCount(uid, 1L)
        return docRef.id
    }


    suspend fun getAllCloudNotes(uid: String): List<Map<String, Any>> {
        val snaps = userNotesCollection(uid).get().await()
        return snaps.documents.mapNotNull { d ->
            val raw = d.data ?: return@mapNotNull null
            val m = raw.toMutableMap()


            val tsVal = raw["timestamp"]
            val tsLong: Long? = when (tsVal) {
                is com.google.firebase.Timestamp -> tsVal.toDate().time
                is Number -> tsVal.toLong()
                is Long -> tsVal
                is Int -> tsVal.toLong()
                else -> null
            }

            if (tsLong != null) {
                m["timestamp"] = tsLong
            } else {

                m["timestamp"] = System.currentTimeMillis()
            }

            m["cloudId"] = d.id
            m
        }
    }


    suspend fun deleteCloudNoteByTimestamp(uid: String, timestamp: Long, toleranceMs: Long = 2000L) {
        val col = userNotesCollection(uid)
        val snaps = try {
            col.get().await()
        } catch (e: Exception) {
            android.util.Log.e("SYNC", "Error fetching notes for delete (uid=$uid)", e)
            throw e
        }

        val toDelete = snaps.documents.filter { doc ->
            val rawTs = doc.get("timestamp")
            val tsLong: Long? = when (rawTs) {
                is com.google.firebase.Timestamp -> rawTs.toDate().time
                is Number -> rawTs.toLong()
                else -> null
            }
            if (tsLong == null) {
                false
            } else {
                kotlin.math.abs(tsLong - timestamp) <= toleranceMs
            }
        }

        if (toDelete.isEmpty()) {
            android.util.Log.w("SYNC", "No matching cloud note found to delete for uid=$uid timestamp=$timestamp (checked ${snaps.size()} docs)")
            return
        }

        for (doc in toDelete) {
            try {
                android.util.Log.d("SYNC", "Deleting cloud note docId=${doc.id} (docTs=${doc.get("timestamp")}) for uid=$uid")
                col.document(doc.id).delete().await()
                android.util.Log.d("SYNC", "Deleted cloud note docId=${doc.id}")
            } catch (e: Exception) {
                android.util.Log.e("SYNC", "Failed deleting cloud note docId=${doc.id}", e)
                throw e
            }
        }
    }
}