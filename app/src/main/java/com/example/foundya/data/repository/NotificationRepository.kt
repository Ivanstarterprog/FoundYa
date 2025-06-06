package com.example.foundya.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

interface NotificationRepository {
    suspend fun saveToken(token: String)
    suspend fun sendClaimNotification(postId: String, claimantName: String)
}

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging
) : NotificationRepository {

    override suspend fun saveToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("user_tokens")
            .document(userId)
            .set(mapOf("token" to token, "timestamp" to FieldValue.serverTimestamp()))
            .await()
    }

    companion object {
        private const val TAG = "NotificationRepo"
        private const val FCM_URL = "https://fcm.googleapis.com/fcm/send"
        private const val CONTENT_TYPE = "application/json"
    }

    private fun getFirebaseServerKey(): String {
        return try {
            val resources = Firebase.app.applicationContext.resources
            val resourceId = resources.getIdentifier(
                "gcm_defaultSenderId",
                "string",
                Firebase.app.applicationContext.packageName
            )
            resources.getString(resourceId)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка получения FCR-ключа ", e)
            ""
        }
    }

    override suspend fun sendClaimNotification(postId: String, claimantName: String) {
        try {
            val post = firestore.collection("posts").document(postId).get().await()

            val ownerToken = post.getString("ownerToken") ?: run {
                Log.w(TAG, "Не найден автор поста $postId")
                return
            }

            val notificationBody = JSONObject().apply {
                put("to", ownerToken)
                put("priority", "high")
                put("notification", JSONObject().apply {
                    put("title", "Нашелся владелец!")
                    put("body", "$claimantName утверждает, что это его вещь")
                    put("sound", "default")
                })
                put("data", JSONObject().apply {
                    put("type", "claim")
                    put("postId", postId)
                    put("claimantName", claimantName)
                })
            }.toString()

            sendFcmNotification(notificationBody)
        } catch (e: Exception) {
            Log.e(TAG, "Не получилось отправить уведомление", e)
        }
    }

    suspend fun sendFcmNotification(body: String) = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(FCM_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "key=${getFirebaseServerKey()}")
                setRequestProperty("Content-Type", CONTENT_TYPE)
                doOutput = true
                doInput = true
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(body)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "FCM error: ${connection.responseMessage}")
            } else {
                Log.d(TAG, "Notification sent successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "FCM request failed", e)
        } finally {
            connection?.disconnect()
        }
    }
}