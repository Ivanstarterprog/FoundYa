package com.example.foundya.data.model

import com.google.firebase.Timestamp

// Post.kt
data class Post(
    val id: String = "",
    val type: String, // "found" или "lost"
    val title: String,
    val description: String,
    val location: String,
    val contact: String,
    val imageUrl: String? = null,
    val timestamp: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Post {
            return Post(
                id = map["id"] as? String ?: "",
                type = map["type"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                location = map["location"] as? String ?: "",
                contact = map["contact"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String,
                timestamp = map["timestamp"] as? Timestamp ?: Timestamp.now()
            )
        }

        fun toMap(post: Post): Map<String, Any> {
            return mapOf(
                "type" to post.type,
                "title" to post.title,
                "description" to post.description,
                "location" to post.location,
                "contact" to post.contact,
                "imageUrl" to (post.imageUrl ?: ""),
                "timestamp" to post.timestamp
            )
        }
    }
}