package com.uvg.rachapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uvg.rachapp.data.model.News
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val newsCollection = firestore.collection("news")

    suspend fun getAllNews(): Result<List<News>> {
        return try {
            val snapshot = newsCollection
                .orderBy("order")
                .get()
                .await()

            val newsList = snapshot.documents.mapNotNull { doc ->
                News(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    shortDesc = doc.getString("shortDesc") ?: "",
                    fullText = doc.getString("fullText") ?: "",
                    order = doc.getLong("order")?.toInt() ?: 0,
                    openCount = doc.getLong("openCount")?.toInt() ?: 0,
                    imageResName = "news${doc.getLong("order")?.toInt() ?: 1}"
                )
            }
            Result.success(newsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementOpenCount(newsId: String): Result<Unit> {
        return try {
            val docRef = newsCollection.document(newsId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentCount = snapshot.getLong("openCount") ?: 0
                transaction.update(docRef, "openCount", currentCount + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}