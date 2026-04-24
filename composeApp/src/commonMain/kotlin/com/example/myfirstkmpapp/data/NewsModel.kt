package com.example.myfirstkmpapp.data

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val status: String? = null,
    val totalResults: Int = 0,
    val articles: List<Article> = emptyList()
)

@Serializable
data class Article(
    val title: String? = null,
    val description: String? = null,
    val urlToImage: String? = null,
    val url: String? = null
)