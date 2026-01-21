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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class RoutineDetailsViewModel @Inject constructor(
    private val repository: RoutineRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- ESTADOS INTERNOS ---
    private var routineId: Int = 0


    private val _routine = MutableStateFlow<RoutineEntity?>(null)
    val routine: StateFlow<RoutineEntity?> = _routine.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    init {

        savedStateHandle.get<Int>("routineId")?.let { id ->
            if (id != 0) {
                loadRoutine(id)
            }
        }
    }


    fun loadRoutine(id: Int) {
        // Evita recargar si ya tenemos los datos para el mismo ID
        if (id == this.routineId && _routine.value != null) return

        this.routineId = id

        viewModelScope.launch {
            repository.getRoutineById(id).collect { routineData ->
                _routine.value = routineData
            }
        }

        viewModelScope.launch {
            repository.getTasksForRoutine(id).collect { tasksData ->
                _tasks.value = tasksData
            }
        }
    }





    fun addTask(name: String, description: String, time: Int) {
        if (name.isBlank()) return
        val newTask = TaskEntity(
            name = name,
            description = description,
            time = time,
            routineId = this.routineId
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
        val currentRoutine = _routine.value ?: return

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
            val currentRoutine = routine.value ?: return@launch
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




