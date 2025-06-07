package com.example.foundya.ui.Composables.AddPost

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foundya.data.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostDialog(
    onDismiss: () -> Unit,
    onConfirm: (Post) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val transition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        transition.animateTo(1f, animationSpec = tween(300))
    }
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically { height -> height } + fadeIn(),
        exit = slideOutVertically { height -> height } + fadeOut()
    ) {
        BasicAlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Добавить потеряшку",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Место") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Контакты") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { /* Логика выбора изображения */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить фото")
                    }

                    selectedImage?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Выбранное изображение",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Отмена")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val newPost = Post(
                                    title = title,
                                    description = description,
                                    location = location,
                                    contact = contact,
                                    imageUrl = selectedImage?.toString(),
                                    type = "lost"
                                )
                                onConfirm(newPost)
                            }
                        ) {
                            Text("Сохранить")
                        }
                    }
                }
            }
        }

    }

}