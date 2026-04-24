package com.example.myfirstkmpapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstkmpapp.data.Article
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun NewsDetailScreen(article: Article) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        article.urlToImage?.let { imageUrl ->
            KamelImage(
                resource = asyncPainterResource(data = imageUrl),
                contentDescription = "Gambar Berita Detail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(250.dp)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = article.title ?: "Tanpa Judul",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article.description ?: "Tidak ada isi berita.",
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Sumber URL: ${article.url ?: "Tidak diketahui"}",
                fontSize = 12.sp,
                color = Color.Blue
            )
        }
    }
}