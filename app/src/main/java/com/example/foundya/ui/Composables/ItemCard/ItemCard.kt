package com.example.foundya.ui.Composables.ItemCard

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foundya.ui.theme.FoundYaTheme

@Composable
fun ItemCard(
    photoUrl: String,
    itemName: String,
    itemDescription: String
){
    Card {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = itemName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(100.dp).height(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Text(
            text = itemName,
            style = MaterialTheme.typography.labelLarge

        )
        Text(
            text = itemDescription,
            style = MaterialTheme.typography.labelMedium
        )

    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardPreview(){
    FoundYaTheme{
        ItemCard(
            photoUrl = "https://i.ytimg.com/vi/PzUSuKW8_ro/maxresdefault.jpg",
            itemName = "Тестовый предмет",
            itemDescription = "Нашел такую штуку когда делал приложуху, лол"
        )
    }
}