package com.uvg.rachapp.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.rachapp.data.local.db.daos.MarkedDayDao
import kotlinx.coroutines.launch
import java.time.LocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.first
import com.uvg.rachapp.data.local.db.entities.StreakDayEntity

data class CalendarUiState(
    val currentBlockIndex: Int = 0,
    val selectedDate: LocalDate = LocalDate.now(),
    val streakDays: Set<LocalDate> = emptySet(),
    val currentStreak: Int = 0,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(private val dao: MarkedDayDao) : ViewModel() {
    private val _uiState = mutableStateOf(CalendarUiState())
    val uiState: State<CalendarUiState> = _uiState

    init {
        viewModelScope.launch {
            val streakDays = dao.getAllMarkedDays().first().map { LocalDate.parse(it.date) }.toSet()
            val currentStreak = calculateCurrentStreak(streakDays)
            _uiState.value = CalendarUiState(
                currentBlockIndex = 0,
                selectedDate = LocalDate.now(),
                streakDays = streakDays,
                currentStreak = currentStreak
            )
        }
    }

    // Calcula la racha actual: días consecutivos desde hoy hacia atrás
    private fun calculateCurrentStreak(markedDays: Set<LocalDate>): Int {
        var streak = 0
        var currentDate = LocalDate.now()
        
        // Si hoy no está marcado, la racha es 0
        if (!markedDays.contains(currentDate)) {
            return 0
        }
        
        // Contar días consecutivos hacia atrás desde hoy
        while (markedDays.contains(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }
        
        return streak
    }

    // Genera un bloque de 8 días basado en el índice del bloque
    fun getCurrentBlockDays(): List<LocalDate> {
        val blockIndex = _uiState.value.currentBlockIndex
        val startDate = LocalDate.now().plusDays((blockIndex * 8).toLong())
        return (0..7).map { startDate.plusDays(it.toLong()) }
    }

    fun selectDate(date: LocalDate) {
        // Si selecciona el día de hoy, resetear el índice de bloques a 0
        val newBlockIndex = if (date == LocalDate.now()) 0 else _uiState.value.currentBlockIndex
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            currentBlockIndex = newBlockIndex
        )
    }

    fun goToNextBlock() {
        _uiState.value = _uiState.value.copy(
            currentBlockIndex = _uiState.value.currentBlockIndex + 1
        )
    }

    fun goToPreviousBlock() {
        _uiState.value = _uiState.value.copy(
            currentBlockIndex = _uiState.value.currentBlockIndex - 1
        )
    }

    fun markTodayAsCompleted() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayString = today.toString() // Formato "YYYY-MM-DD"
            
            // Insertar en la base de datos
            dao.insertMarkedDay(StreakDayEntity(date = todayString))
            
            // Actualizar el estado para reflejar el cambio
            val updatedStreakDays = _uiState.value.streakDays + today
            val newStreak = calculateCurrentStreak(updatedStreakDays)
            _uiState.value = _uiState.value.copy(
                streakDays = updatedStreakDays,
                currentStreak = newStreak
            )
        }
    }

    fun removeTodayFromStreak() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayString = today.toString() // Formato "YYYY-MM-DD"
            
            // Eliminar de la base de datos
            dao.deleteMarkedDayByDate(todayString)
            
            // Actualizar el estado para reflejar el cambio
            val updatedStreakDays = _uiState.value.streakDays - today
            val newStreak = calculateCurrentStreak(updatedStreakDays)
            _uiState.value = _uiState.value.copy(
                streakDays = updatedStreakDays,
                currentStreak = newStreak
            )
        }
    }
}



















