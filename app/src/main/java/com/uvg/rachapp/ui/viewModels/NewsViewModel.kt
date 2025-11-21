package com.uvg.rachapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.rachapp.data.model.News
import com.uvg.rachapp.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsUiState(
    val newsList: List<News> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val expandedIndex: Int? = null,
    val isDialogOpen: Boolean = false,
    val selectedNews: News? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            newsRepository.getAllNews()
                .onSuccess { news ->
                    _uiState.value = _uiState.value.copy(
                        newsList = news,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun onNewsClick(index: Int) {
        val currentState = _uiState.value

        // Si ya está expandido, abrir el diálogo completo
        if (currentState.expandedIndex == index) {
            val news = currentState.newsList[index]
            _uiState.value = currentState.copy(
                isDialogOpen = true,
                selectedNews = news
            )
            // Incrementar contador en Firebase
            viewModelScope.launch {
                newsRepository.incrementOpenCount(news.id)
            }
        } else {
            // Primera vez: solo expandir
            _uiState.value = currentState.copy(expandedIndex = index)
        }
    }

    fun onCollapseNews() {
        _uiState.value = _uiState.value.copy(expandedIndex = null)
    }

    fun onCloseDialog() {
        _uiState.value = _uiState.value.copy(
            isDialogOpen = false,
            selectedNews = null,
            expandedIndex = null
        )
    }

    fun retryLoadNews() {
        loadNews()
    }
}