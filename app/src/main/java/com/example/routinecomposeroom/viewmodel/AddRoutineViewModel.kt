package com.example.routinecomposeroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.RoutineEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

// Estado que representa la UI del diálogo
data class AddRoutineState(
    val name: String = "",
    val description: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val startHour: LocalTime = LocalTime.now(),
    val frequency: Frequency = Frequency.DAILY,
    val totalTimes: String = "1",
    val expandedFrequency: Boolean = false
)

@HiltViewModel
class AddRoutineViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddRoutineState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onDateChange(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun onHourChange(time: LocalTime) {
        _uiState.update { it.copy(startHour = time) }
    }

    fun onFrequencyChange(freq: Frequency) {
        _uiState.update { it.copy(frequency = freq, expandedFrequency = false) }
    }

    fun onTotalTimesChange(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(totalTimes = value) }
        }
    }

    fun onToggleFrequencyMenu(expand: Boolean) {
        _uiState.update { it.copy(expandedFrequency = expand) }
    }

    // Función para crear la entidad rutina a partir del estado
    fun toRoutineEntity(): RoutineEntity? {
        val state = _uiState.value
        return if (state.name.isNotBlank() && (state.totalTimes.toIntOrNull() ?: 0) > 0) {
            RoutineEntity(
                name = state.name,
                description = state.description,
                startDate = state.startDate,
                startHour = state.startHour,
                frequency = state.frequency,
                totalTimes = state.totalTimes.toInt()
            )
        } else {
            null
        }
    }
}
