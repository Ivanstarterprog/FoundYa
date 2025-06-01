package com.example.foundya.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ClaimRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun createClaim(postId: String, contact: String) {
        val claim = hashMapOf(
            "postId" to postId,
            "contact" to contact,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("claims")
            .add(claim)
            .addOnSuccessListener { /* Успех */ }
            .addOnFailureListener { /* Ошибка */ }
    }
}