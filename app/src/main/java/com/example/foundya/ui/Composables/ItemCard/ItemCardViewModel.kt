package com.example.foundya.ui.Composables.ItemCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundya.data.model.Post
import com.example.foundya.data.repository.ImageRepository
import com.example.foundya.data.repository.NotificationRepository
import com.example.foundya.data.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository,
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth
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
                _posts.value = postRepository.getPosts()
            } catch (e: Exception) {
                TODO()
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
}

data class ClaimState(
    val isClaiming: Boolean = false,
    val isClaimed: Boolean = false,
    val errorMessage: String? = null
)