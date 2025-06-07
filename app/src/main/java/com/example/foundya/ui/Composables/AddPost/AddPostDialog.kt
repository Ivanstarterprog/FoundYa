package com.example.foundya.ui.Composables.AddPost

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foundya.data.model.Post
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    var postType by remember { mutableStateOf("Потеряшка") }
    var isTypeMenuExpanded by remember { mutableStateOf(false) }
    val postTypes = listOf("Потеряшка", "Находка")

    val transition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        transition.animateTo(1f, animationSpec = tween(300))
    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { selectedImage = it }
        }
    )

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDate by remember(selectedDate) {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd.MM.yyyy")
                .format(selectedDate)
        }
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Добавить объявление",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    ExposedDropdownMenuBox(
                        expanded = isTypeMenuExpanded,
                        onExpandedChange = { isTypeMenuExpanded = !isTypeMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = postType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Тип объявления") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeMenuExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = isTypeMenuExpanded,
                            onDismissRequest = { isTypeMenuExpanded = false }
                        ) {
                            postTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        postType = type
                                        isTypeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

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

                    OutlinedTextField(
                        value = formattedDate,
                        onValueChange = {},
                        label = { Text("Дата находки") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Выбрать дату"
                                )
                            }
                        }
                    )

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate
                                .atStartOfDay()
                                .toInstant(java.time.ZoneOffset.UTC)
                                .toEpochMilli()
                        )

                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        datePickerState
                                            .selectedDateMillis
                                            ?.let { millis ->
                                                selectedDate =
                                                    LocalDate.ofEpochDay(millis / 86400000)
                                            }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Button(
                        onClick = {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedImage == null) "Добавить фото" else "Изменить фото")
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

                        Button(
                            onClick = { selectedImage = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Удалить фото")
                        }
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
                                    type = if (postType == "Потеряшка") "lost" else "found"
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