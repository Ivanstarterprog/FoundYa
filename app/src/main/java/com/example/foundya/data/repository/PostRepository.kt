package com.example.foundya.data.repository

import com.example.foundya.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PostRepository {

    override suspend fun getPosts(type: String?): List<Post> {
        val query = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val snapshot = if (type != null) {
            query.whereEqualTo("type", type).get().await()
        } else {
            query.get().await()
        }

        return snapshot.documents.mapNotNull { doc ->
            doc.data?.let { data ->
                Post.fromMap(data + ("id" to doc.id))
            }
        }
    }

    override suspend fun createPost(post: Post): String {
        val user = auth.currentUser?.uid ?: throw Exception("Not authenticated")
        val postWithOwner = post.copy(ownerId = user)
        val docRef = firestore.collection("posts").add(Post.toMap(postWithOwner)).await()
        return docRef.id
    }
}

interface PostRepository {
    suspend fun getPosts(type: String? = null): List<Post>
    suspend fun createPost(post: Post): String
}