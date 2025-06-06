package com.example.foundya.data.use_case

import com.example.foundya.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ClaimUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(postId: String) {
        val userName = auth.currentUser?.displayName ?: "Пользователь"
        notificationRepository.sendClaimNotification(postId, userName)
    }
}