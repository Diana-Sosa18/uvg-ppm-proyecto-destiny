package com.uvg.rachapp.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NewsUiState(
    val isDialogOpen: Boolean = false,
    val selectedIndex: Int? = null
)

class NewsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    fun onOpenDialog(index: Int) {
        _uiState.value = _uiState.value.copy(isDialogOpen = true, selectedIndex = index)
    }

    fun onCloseDialog() {
        _uiState.value = _uiState.value.copy(isDialogOpen = false, selectedIndex = null)
    }
}