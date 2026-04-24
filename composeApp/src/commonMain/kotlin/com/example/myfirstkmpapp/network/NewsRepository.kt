package com.example.myfirstkmpapp.network

import com.example.myfirstkmpapp.data.Article
import com.example.myfirstkmpapp.data.NewsResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class NewsRepository(private val client: HttpClient) {
    private val apiKey = Secrets.NEWS_API_KEY
    private val baseUrl = "https://newsapi.org/v2"

    suspend fun getTopHeadlines(): Result<List<Article>> {
        return try {
            val response: NewsResponse = client.get("$baseUrl/top-headlines") {
                url {
                    parameters.append("country", "us")
                    parameters.append("category", "technology")
                    parameters.append("apiKey", apiKey)
                }
            }.body()

            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}