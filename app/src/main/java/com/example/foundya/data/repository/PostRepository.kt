package com.example.foundya.data.repository

import android.util.Log
import com.example.foundya.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) : PostRepository {

    override suspend fun getPosts(type: String?): List<Post> {
        return try {
            Log.d("FirestoreDebug", "1. Начало загрузки постов. Тип: $type")

            val query = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .also { Log.d("FirestoreDebug", "2. Запрос построен") }

            val finalQuery = type?.let {
                Log.d("FirestoreDebug", "3. Добавляем фильтр по типу: $it")
                query.whereEqualTo("type", it)
            } ?: query.also { Log.d("FirestoreDebug", "3. Без фильтра по типу") }

            Log.d("FirestoreDebug", "4. Выполняем запрос...")
            val snapshot = finalQuery.get().await()
            Log.d("FirestoreDebug", "5. Получено документов: ${snapshot.documents.size}")

            val posts = snapshot.documents.mapNotNull { doc ->
                Log.d("FirestoreDebug", "6. Обработка документа ID: ${doc.id}")
                doc.data?.let { data ->
                    Post.fromMap(data + ("id" to doc.id)).also {
                        Log.d("FirestoreDebug", "7. Успешно преобразован пост: ${it.title}")
                    }
                } ?: run {
                    Log.e("FirestoreDebug", "7. Документ ${doc.id} имеет пустые данные")
                    null
                }
            }

            Log.d("FirestoreDebug", "8. Всего загружено постов: ${posts.size}")
            posts
        } catch (e: Exception) {
            Log.e("FirestoreDebug", "Ошибка при загрузке: ${e.javaClass.simpleName}: ${e.message}")
            emptyList()
        }
    }

    override suspend fun createPost(post: Post): String {
        val user = auth.currentUser?.uid ?: throw Exception("Не авторизированны")

        val ownerToken = try {
            messaging.token.await()
        } catch (e: Exception) {
            Log.e("PostRepository", "Ошибка получения токена", e)
            null
        }

        val postWithOwner = post.copy(
            ownerId = user,
            ownerToken = ownerToken
        )

        val docRef = firestore.collection("posts").add(Post.toMap(postWithOwner)).await()
        return docRef.id
    }

    override suspend fun deletePost(postId: String) {
        try {
            firestore.collection("posts").document(postId).delete().await()
        } catch (e: Exception) {
            throw Exception("Ошибка при удалении поста: ${e.message}")
        }
    }

    override suspend fun getPostById(postId: String): Post {
        val snapshot = firestore.collection("posts").document(postId).get().await()
        return snapshot.data?.let { data ->
            Post.fromMap(data + ("id" to snapshot.id))
        } ?: throw Exception("Пост не найден")
    }
}

interface PostRepository {
    suspend fun getPosts(type: String? = null): List<Post>
    suspend fun createPost(post: Post): String
    suspend fun getPostById(postId: String): Post
    suspend fun deletePost(postId: String)
}