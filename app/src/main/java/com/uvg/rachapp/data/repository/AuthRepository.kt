package com.uvg.rachapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser?> {
        println("🟢 [REPO] loginUser() started - email: $email")
        return try {
            println("🟢 [REPO] Calling signInWithEmailAndPassword")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            println("🟢 [REPO] Login success - user: ${result.user?.uid}")
            Result.success(result.user)
        } catch (e: Exception) {
            println("🔴 [REPO] ERROR in loginUser: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun registerUser(email: String, password: String, name: String, phone: String): Result<FirebaseUser?> {
        println("🟢 [REPO] registerUser() started - email: $email, name: $name")
        return try {
            println("🟢 [REPO] Calling createUserWithEmailAndPassword")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            println("🟢 [REPO] Firebase Auth success - user: ${authResult.user}, uid: ${authResult.user?.uid}")

            authResult.user?.let { user ->
                println("🟢 [REPO] Saving to Firestore for uid: ${user.uid}")
                val userData = hashMapOf(
                    "id" to user.uid,
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                db.collection("users").document(user.uid).set(userData).await()
                println("🟢 [REPO] Firestore save completed")
            }

            println("🟢 [REPO] Returning SUCCESS with user: ${authResult.user}")
            Result.success(authResult.user)

        } catch (e: Exception) {
            println("🔴 [REPO] ERROR in registerUser: ${e.message}")
            println("🔴 [REPO] Error type: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    suspend fun registerWithFacebook(email: String, password: String): Result<FirebaseUser?> {
        println("🟢 [REPO] registerWithFacebook() called")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithGmail(email: String, password: String): Result<FirebaseUser?> {
        println("🟢 [REPO] registerWithGmail() called")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUser
    fun signOut() = auth.signOut()
}