package com.uvg.rachapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.rachapp.repository.AuthRepository
import com.uvg.rachapp.data.repository.NoteRepository
import com.uvg.rachapp.data.repository.MarkedDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository,
    private val markedDayRepository: MarkedDayRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // Notas creadas
    val notesCount: StateFlow<Int> = noteRepository.getAllNotes()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Cargar marcados desde la base de datos
    private val allMarkedDays: StateFlow<Set<LocalDate>> = markedDayRepository.getAllMarkedDays()
        .map { list -> list.map { LocalDate.parse(it.date) }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    // Racha actual
    val currentStreak: StateFlow<Int> = allMarkedDays
        .map { markedDays -> calculateCurrentStreak(markedDays) }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Racha más larga
    val longestStreak: StateFlow<Int> = allMarkedDays
        .map { markedDays -> calculateMaxStreak(markedDays) }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Días alimentando a Destiny (= días marcados)
    val daysFed: StateFlow<Int> = allMarkedDays
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()

            _userEmail.value = user?.email ?: ""
            _userName.value =
                authRepository.getUserName(user?.uid)
                ?: user?.email?.substringBefore("@")
                ?: "Desconocido"
        }
    }

    private fun calculateCurrentStreak(markedDays: Set<LocalDate>): Int {
        var streak = 0
        var currentDate = LocalDate.now()
        if (!markedDays.contains(currentDate)) {
            return 0
        }
        while (markedDays.contains(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }
        return streak
    }

    private fun calculateMaxStreak(markedDays: Set<LocalDate>): Int {
        if (markedDays.isEmpty()) return 0
        var max = 0
        for (date in markedDays) {
            if (!markedDays.contains(date.minusDays(1))) {
                var len = 1
                var current = date
                while (markedDays.contains(current.plusDays(1))) {
                    len++
                    current = current.plusDays(1)
                }
                if (len > max) max = len
            }
        }
        return max
    }

    // Estado del diálogo
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    fun onLogoutClick() {
        _showLogoutDialog.value = true
    }

    fun onDialogConfirmLogout(confirmAction: suspend () -> Unit) {
        _showLogoutDialog.value = false
    }

    fun getStreakHelpMessage(): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val markedDays = allMarkedDays.value

        val todayMarked = markedDays.contains(today)
        val yesterdayMarked = markedDays.contains(yesterday)

        return when {
            todayMarked -> {
                "Destiny está bien alimentado. ¡Lo estás haciendo muy bien!"
            }
            currentStreak.value == 0 && yesterdayMarked -> {
                "¡Estás a tiempo de cuidar de Destiny! Aliméntalo hoy para recuperar tu racha."
            }
            currentStreak.value == 0 && !yesterdayMarked -> {
                "Nuevo día, nueva oportunidad. ¡Vuelve a alimentar a Destiny para recuperar tu racha!"
            }
            else -> {
                "¡Sigue cuidando de Destiny todos los días!"
            }
        }
    }

    fun onDialogDismiss() {
        _showLogoutDialog.value = false
    }
}