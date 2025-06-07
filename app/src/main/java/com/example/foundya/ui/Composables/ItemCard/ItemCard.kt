package com.example.foundya.ui.Composables.ItemCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.compose.FoundYaTheme
import com.example.foundya.R
import com.example.foundya.data.model.Post

@Composable
fun ItemCard(
    post: Post,
    placeholderUrl: String?,
    onClaimClick: () -> Unit,
    isClaiming: Boolean = false,
    isClaimed: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = post.getImageUrl(placeholderUrl),
                contentDescription = post.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            )
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Местоположение",
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = post.location,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isClaimed) {
                FilledTonalButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Заявка отправлена")
                }
            } else {
                Button(
                    onClick = onClaimClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isClaiming
                ) {
                    if (isClaiming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Это моё!")
                }
            }

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Default PostCard")
@Composable
fun PostCardPreview() {
    FoundYaTheme {
        ItemCard(
            post = Post(
                id = "1",
                type = "found",
                title = "Найден телефон",
                description = "Нашел в парке Горького черный iPhone 13 Pro",
                location = "Парк Горького, центральная аллея",
                contact = "tg: @found_user",
                imageUrl = "https://example.com/phone.jpg"
            ),
            placeholderUrl = "https://NurgazinIvan.pythonanywhere.com/api/uploads/placeholder.jpg",
            onClaimClick = {}
        )
    }
}

@Preview(showBackground = true, name = "PostCard with Placeholder")
@Composable
fun PostCardWithPlaceholderPreview() {
    FoundYaTheme {
        ItemCard(
            post = Post(
                id = "2",
                type = "lost",
                title = "Потерялись ключи",
                description = "Потерял связку ключей с брелоком в виде медведя",
                location = "Метро Университет",
                contact = "8-999-123-45-67",
                imageUrl = "https://NurgazinIvan.pythonanywhere.com/api/uploads/placeholder.jpg"
            ),
            placeholderUrl = "https://NurgazinIvan.pythonanywhere.com/api/uploads/placeholder.jpg",
            onClaimClick = {}
        )
    }
}

@Preview(showBackground = true, name = "PostCard Claiming State")
@Composable
fun PostCardClaimingPreview() {
    FoundYaTheme {
        ItemCard(
            post = Post(
                id = "3",
                type = "found",
                title = "Найден кошелек",
                description = "Кожаный кошелек с документами",
                location = "ТЦ Авиапарк, 3 этаж",
                contact = "email: found@example.com"
            ),
            placeholderUrl = "https://NurgazinIvan.pythonanywhere.com/api/uploads/placeholder.jpg",
            onClaimClick = {},
            isClaiming = true
        )
    }
}

@Preview(showBackground = true, name = "PostCard Claimed State")
@Composable
fun PostCardClaimedPreview() {
    FoundYaTheme {
        ItemCard(
            post = Post(
                id = "4",
                type = "lost",
                title = "Потерялся паспорт",
                description = "Паспорт на имя Иванов И.И.",
                location = "Кафе Шоколадница на Ленинском",
                contact = "89045783556"
            ),
            placeholderUrl = "https://NurgazinIvan.pythonanywhere.com/api/uploads/placeholder.jpg",
            onClaimClick = {},
            isClaimed = true
        )
    }
}

@Preview(showBackground = true, name = "PostCard Error State")
@Composable
fun PostCardErrorPreview() {
    FoundYaTheme {
        ItemCard(
            post = Post(
                id = "5",
                type = "found",
                title = "Найдены документы",
                description = "Водительские права и СТС",
                location = "Остановка Университет",
                contact = "89045783556"
            ),
            placeholderUrl = "https://yourusername.pythonanywhere.com/api/uploads/placeholder.jpg",
            onClaimClick = {},
            errorMessage = "Ошибка сети. Попробуйте позже"
        )
    }
}