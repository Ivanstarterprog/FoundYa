package com.example.foundya.ui.Composables.ItemCard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundya.data.model.Post
import com.example.foundya.data.repository.ImageRepository
import com.example.foundya.data.repository.NotificationRepository
import com.example.foundya.data.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository,
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _placeholderUrl = MutableStateFlow<String?>(null)
    val placeholderUrl: StateFlow<String?> = _placeholderUrl.asStateFlow()

    private val _claimStates = MutableStateFlow<Map<String, ClaimState>>(emptyMap())
    val claimStates: StateFlow<Map<String, ClaimState>> = _claimStates.asStateFlow()

    init {
        loadPlaceholder()
        loadPosts()
    }

    private fun loadPlaceholder() {
        viewModelScope.launch {
            try {
                _placeholderUrl.value = imageRepository.getImageUrl(1)
            } catch (e: Exception) {
                TODO()
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val posts = postRepository.getPosts()
                Log.d("PostListVM", "Loaded posts: ${posts.size}") // Логируем количество
                _posts.value = posts
            } catch (e: Exception) {
                Log.e("PostListVM", "Error loading posts", e) // Логируем ошибку
            }
        }
    }

    fun onClaimClick(postId: String) {
        viewModelScope.launch {
            _claimStates.value += (postId to ClaimState(isClaiming = true))

            try {
                val userName = auth.currentUser?.displayName ?: "Пользователь"
                notificationRepository.sendClaimNotification(postId, userName)

                _claimStates.value += (postId to ClaimState(isClaimed = true))

            } catch (e: Exception) {
                _claimStates.value += (postId to ClaimState(errorMessage = "Ошибка: ${e.message}"))
            }
        }
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            try {
                // 1. Загружаем изображение если есть
                val imageUrl = post.imageUrl?.let { uri ->
                    val file = File(uri)
                    imageRepository.uploadImage(file).url
                }

                val user = auth.currentUser?.uid ?: throw Exception("Не авторизован")
                val ownerToken = try { messaging.token.await() } catch (e: Exception) { null }

                val postWithOwner = post.copy(
                    imageUrl = imageUrl,
                    ownerId = user,
                    ownerToken = ownerToken
                )

                // 3. Сохраняем в Firestore
                val postId = postRepository.createPost(postWithOwner)
                val postWithId = postWithOwner.copy(id = postId)

                // 4. Обновляем UI (ВАЖНО: создаем новый список)
                _posts.value = _posts.value.toMutableList().apply { add(0, postWithId) }

            } catch (e: Exception) {
                // Логируем ошибку
                Log.e("PostListViewModel", "Ошибка добавления поста", e)
            }
        }
    }

    data class ClaimState(
        val isClaiming: Boolean = false,
        val isClaimed: Boolean = false,
        val errorMessage: String? = null
    )
}