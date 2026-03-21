package com.example.routinecomposeroom.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.entities.TaskEntity
import com.example.routinecomposeroom.data.utils.isConcluded
import com.example.routinecomposeroom.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class RoutineDetailsState(
    val routine: RoutineEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RoutineDetailsViewModel @Inject constructor(
    private val repository: RoutineRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineDetailsState())
    val uiState: StateFlow<RoutineDetailsState> = _uiState.asStateFlow()


    init {

        val routineId: Int? = savedStateHandle.get<Int>("routineId")
        if (routineId != null && routineId != 0) {

            loadRoutineAndTasks(routineId)
        } else {

            _uiState.update { it.copy(isLoading = false) }
        }
    }


    fun loadRoutineAndTasks(id: Int) {

        if (savedStateHandle.get<Int>("routineId") == id && !_uiState.value.isLoading) return

        savedStateHandle["routineId"] = id
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getRoutineById(id)
                .combine(repository.getTasksForRoutine(id)) { routineData, tasksData ->
                    RoutineDetailsState(
                        routine = routineData,
                        tasks = tasksData,
                        isLoading = false
                    )
                }
                .collect { combinedState ->
                    _uiState.value = combinedState
                }
        }
    }


    fun addTask(name: String, description: String, time: Int) {
        if (name.isBlank()) return

        val routineId = savedStateHandle.get<Int>("routineId") ?: return

        val newTask = TaskEntity(
            name = name,
            description = description,
            time = time,
            routineId = routineId
        )

        viewModelScope.launch {
            repository.insertTask(newTask)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateRoutineDetails(
        newName: String,
        newDescription: String,
        newStartDate: LocalDate,
        newStartHour: LocalTime,
        newFrequency: Frequency,
        newTotalTimes: Int
    ) {
        val currentRoutine = _uiState.value.routine ?: return

        val updatedRoutine = currentRoutine.copy(
            name = newName,
            description = newDescription,
            startDate = newStartDate,
            startHour = newStartHour,
            frequency = newFrequency,
            totalTimes = newTotalTimes
        )

        viewModelScope.launch {
            repository.updateRoutine(updatedRoutine)
        }
    }

    fun updateTask(task: TaskEntity, newName: String, newDescription: String, newTime: Int) {
        val updatedTask = task.copy(
            name = newName,
            description = newDescription,
            time = newTime
        )

        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }

    fun onFinishRoutineClicked() {
        viewModelScope.launch {
            val currentRoutine = _uiState.value.routine ?: return@launch
            val today = LocalDate.now()

            if (!currentRoutine.isConcluded && currentRoutine.canBeCompletedToday) {
                val updatedRoutine = currentRoutine.copy(
                    timesDone = currentRoutine.timesDone + 1,
                    lastCompletedDate = today
                )
                repository.updateRoutine(updatedRoutine)
            }
        }
    }
}






