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

import java.time.temporal.ChronoUnit
import java.time.format.TextStyle
import java.util.Locale
data class CalendarUiState(
    val currentBlockIndex: Int = 0,
    val selectedDate: LocalDate = LocalDate.now(),
    val streakDays: Set<LocalDate> = emptySet(),
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    // separate index for month-based blocks (used by expanded calendar)
    val currentMonthBlockIndex: Int = 0,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(private val dao: MarkedDayDao) : ViewModel() {
    private val _uiState = mutableStateOf(CalendarUiState())
    val uiState: State<CalendarUiState> = _uiState

    init {
        viewModelScope.launch {
            val streakDays = dao.getAllMarkedDays().first().map { LocalDate.parse(it.date) }.toSet()
            val currentStreak = calculateCurrentStreak(streakDays)
            val maxStreak = calculateMaxStreak(streakDays)
            _uiState.value = CalendarUiState(
                currentBlockIndex = 0,
                selectedDate = LocalDate.now(),
                streakDays = streakDays,
                currentStreak = currentStreak,
                maxStreak = maxStreak
            )
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

    fun getMonthBlockDays(monthOffset: Int = _uiState.value.currentMonthBlockIndex): List<LocalDate> {
        // monthOffset 0 -> current month, 1 -> next month, -1 -> previous month, etc.
        val target = LocalDate.now().withDayOfMonth(1).plusMonths(monthOffset.toLong())
        // determine the start of the calendar grid (week starts on Monday)
        val dayOfWeekValue = target.dayOfWeek.value // 1 = Monday ... 7 = Sunday
        val start = target.minusDays((dayOfWeekValue - 1).toLong())
        return (0..34).map { start.plusDays(it.toLong()) }
    }

    fun getCurrentBlockDays(): List<LocalDate> {
        val blockIndex = _uiState.value.currentBlockIndex
        val startDate = LocalDate.now().plusDays((blockIndex * 8).toLong())
        return (0..7).map { startDate.plusDays(it.toLong()) }
    }

    fun selectDate(date: LocalDate) {
        val newBlockIndex = if (date == LocalDate.now()) 0 else _uiState.value.currentBlockIndex
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            currentBlockIndex = newBlockIndex
        )
    }

    fun getCurrentMonthBlockDays(): List<LocalDate> {
        return getMonthBlockDays(_uiState.value.currentMonthBlockIndex)
    }

    fun getMonthTitleForBlock(monthOffset: Int = _uiState.value.currentMonthBlockIndex): String {
        val target = LocalDate.now().withDayOfMonth(1).plusMonths(monthOffset.toLong())
        val month = target.month.getDisplayName(TextStyle.FULL, Locale("es"))
        val year = target.year
        return "${month.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }} de $year"
    }

    fun goToNextMonthBlock() {
        _uiState.value = _uiState.value.copy(
            currentMonthBlockIndex = _uiState.value.currentMonthBlockIndex + 1
        )
    }

    fun goToPreviousMonthBlock() {
        _uiState.value = _uiState.value.copy(
            currentMonthBlockIndex = _uiState.value.currentMonthBlockIndex - 1
        )
    }

    fun setMonthBlockIndex(index: Int) {
        _uiState.value = _uiState.value.copy(currentMonthBlockIndex = index)
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


    fun isFirstInStreak(date: LocalDate): Boolean {
        val marked = _uiState.value.streakDays
        if (!marked.contains(date)) return false
        return !marked.contains(date.minusDays(1))
    }

    fun isLastInStreak(date: LocalDate): Boolean {
        val marked = _uiState.value.streakDays
        if (!marked.contains(date)) return false
        return !marked.contains(date.plusDays(1))
    }

    fun isSingleInStreak(date: LocalDate): Boolean {
        return isFirstInStreak(date) && isLastInStreak(date)
    }

    fun markTodayAsCompleted() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayString = today.toString()
            dao.insertMarkedDay(StreakDayEntity(date = todayString))
            val updatedStreakDays = _uiState.value.streakDays + today
            val newStreak = calculateCurrentStreak(updatedStreakDays)
            val newMax = calculateMaxStreak(updatedStreakDays)
            _uiState.value = _uiState.value.copy(
                streakDays = updatedStreakDays,
                currentStreak = newStreak,
                maxStreak = newMax
            )
        }
    }

    fun removeTodayFromStreak() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayString = today.toString()
            dao.deleteMarkedDayByDate(todayString)
            val updatedStreakDays = _uiState.value.streakDays - today
            val newStreak = calculateCurrentStreak(updatedStreakDays)
            val newMax = calculateMaxStreak(updatedStreakDays)
            _uiState.value = _uiState.value.copy(
                streakDays = updatedStreakDays,
                currentStreak = newStreak,
                maxStreak = newMax
            )
        }
    }
}