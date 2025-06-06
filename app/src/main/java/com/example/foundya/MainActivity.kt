package com.example.foundya

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.foundya.data.model.Post
import com.example.foundya.data.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.foundya.ui.theme.FoundYaTheme
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var postRepository: PostRepository
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth

        setContent {
            FoundYaTheme {
                val posts = remember { mutableStateListOf<Post>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Greeting(name = "Firebase Tester")

                        Button(onClick = { testAuthentication() }) {
                            Text("Test Authentication")
                        }

                        Button(onClick = { testCreatePost() }) {
                            Text("Create Test Post")
                        }

                        Button(onClick = {
                            lifecycleScope.launch {
                                posts.clear()
                                posts.addAll(postRepository.getPosts())
                            }
                        }) {
                            Text("Load Posts")
                        }

                        PostsList(posts = posts)
                    }
                }
            }
        }
    }

    private fun testAuthentication() {
        val email = "test@example.com"
        val password = "password123"

        lifecycleScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Log.d("FIREBASE_TEST", "Authentication SUCCESS! User ID: ${auth.currentUser?.uid}")
            } catch (e: Exception) {
                Log.e("FIREBASE_TEST", "Authentication failed: ${e.message}")
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    Log.d("FIREBASE_TEST", "User created! User ID: ${auth.currentUser?.uid}")
                } catch (e: Exception) {
                    Log.e("FIREBASE_TEST", "User creation failed: ${e.message}")
                }
            }
        }
    }

    private fun testCreatePost() {
        lifecycleScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("Not authenticated")

                val testPost = Post(
                    type = "found",
                    title = "Найден телефон",
                    description = "Найден в аудитории 305, корпус Б",
                    location = "Корпус Б, 3 этаж",
                    contact = "test@example.com",
                    ownerId = userId
                )

                val postId = postRepository.createPost(testPost)
                Log.d("FIREBASE_TEST", "Post created! ID: $postId")
            } catch (e: Exception) {
                Log.e("FIREBASE_TEST", "Post creation failed: ${e.message}")
            }
        }
    }
}

@Composable
fun PostsList(posts: List<Post>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("Loaded Posts: ${posts.size}")
        posts.forEach { post ->
            Text("• ${post.title} (${post.type})")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoundYaTheme {
        Greeting("Android")
    }
}

