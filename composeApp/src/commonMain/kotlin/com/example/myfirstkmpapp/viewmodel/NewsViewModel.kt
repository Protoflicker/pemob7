package com.example.myfirstkmpapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstkmpapp.data.Article
import com.example.myfirstkmpapp.network.NewsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            delay(1000)
            repository.getTopHeadlines()
                .onSuccess { articles ->
                    val validArticles = articles.filter {
                        !it.urlToImage.isNullOrEmpty() && it.title != "[Removed]"
                    }
                    _uiState.value = NewsUiState.Success(validArticles)
                }
                .onFailure { error ->
                    _uiState.value = NewsUiState.Error(error.message ?: "Gagal memuat berita. Periksa koneksi internet Anda.")
                }
        }
    }
}