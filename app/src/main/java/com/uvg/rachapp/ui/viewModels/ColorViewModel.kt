package com.uvg.rachapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ColorViewModel @Inject constructor(
    private val repo: ColorPreferencesRepository
) : ViewModel() {
    // expose a single integer selection (1..3)
    val selection: StateFlow<Int> = repo.selection

    fun setSelection(value: Int) {
        viewModelScope.launch { repo.setSelection(value) }
    }
}
