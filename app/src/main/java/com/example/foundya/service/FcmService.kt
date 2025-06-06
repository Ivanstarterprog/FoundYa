package com.example.foundya.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.foundya.MainActivity
import com.example.foundya.R
import com.example.foundya.data.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: NotificationRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            try {
                repository.saveToken(token)
                Log.d("FCM", "Token saved successfully: $token")
            } catch (e: Exception) {
                Log.e("FCM", "Failed to save token", e)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.let { data ->
            val type = data["type"]
            val postId = data["postId"]
            val claimantName = data["claimantName"]

            if (type == "claim" && postId != null && claimantName != null) {
                showClaimNotification(postId, claimantName)
            } else {
                message.notification?.let { notification ->
                    showNotification(
                        title = notification.title ?: "FoundYa",
                        body = notification.body ?: getString(R.string.NewMessage)
                    )
                }
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "foundya_general_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelId, "General Notifications", "Общие уведомления")

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel(channelId: String, channelName: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            this.description = description
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun showClaimNotification(postId: String, claimantName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("postId", postId)
            putExtra("notificationType", "claim")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "claims_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Нашелся владелец!")
            .setContentText("$claimantName утверждает, что это его вещь")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Заявки на вещи",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомления о заявках на ваши вещи"
        }
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}