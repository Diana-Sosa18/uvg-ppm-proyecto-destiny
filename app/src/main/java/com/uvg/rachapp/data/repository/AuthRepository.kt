package com.uvg.rachapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.rachapp.data.repository.NoteRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val noteRepository: NoteRepository
) {
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerUser(email: String, password: String, name: String, phone: String): Result<FirebaseUser?> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            authResult.user?.let { user ->
                val userData = hashMapOf(
                    "id" to user.uid,
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                db.collection("users").document(user.uid).set(userData).await()
            }

            Result.success(authResult.user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithFacebook(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithGmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUser
    fun signOut() = auth.signOut()

    suspend fun signOutAndClear() {
        auth.signOut()
        try {
            noteRepository.clearAllNotes()
        } catch (_: Exception) {
        }
    }
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            val userQuery = db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            android.util.Log.d("AuthRepository", "Query successful. isEmpty: ${userQuery.isEmpty}")

            if (userQuery.isEmpty) {
                Result.failure(Exception("No existe una cuenta registrada con este correo electrónico"))
            } else {
                auth.sendPasswordResetEmail(email).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error: ${e.message}", e)
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun getUserName(uid: String?): String? {
        if (uid == null) return null

        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.getString("name")
        } catch (e: Exception) {
            null
        }
    }
}