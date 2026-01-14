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

    // Usaremos MutableStateFlow para poder emitir los datos de la rutina cargada
    private val _routine = MutableStateFlow<RoutineEntity?>(null)
    val routine: StateFlow<RoutineEntity?> = _routine.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    init {
        // Intenta cargar el ID desde la navegación principal (útil si se abre la pantalla completa)
        savedStateHandle.get<Int>("routineId")?.let { id ->
            if (id != 0) { // Si el ID es válido
                loadRoutine(id)
            }
        }
    }

    // --- FUNCIÓN DE CARGA ---
    // Esta función se llamará desde el diálogo de edición para cargar los datos
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

    // --- ACCIONES DEL USUARIO ---



    fun addTask(name: String, description: String, time: Int) {
        if (name.isBlank()) return
        val newTask = TaskEntity(
            name = name,
            description = description, // Usamos la descripción del formulario
            time = time,             // Usamos el tiempo del formulario
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

    // --- AÑADIR ESTA FUNCIÓN PARA ACTUALIZAR UNA TAREA ---
    fun updateTask(task: TaskEntity, newName: String, newDescription: String, newTime: Int) {
        // Creamos una copia de la tarea con los datos nuevos
        val updatedTask = task.copy(
            name = newName,
            description = newDescription,
            time = newTime
        )
        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }


    // En RoutineDetailsViewModel.kt

    // --> REEMPLAZA ESTA FUNCIÓN COMPLETA <--
    fun onFinishRoutineClicked() {
        viewModelScope.launch {
            val currentRoutine = routine.value ?: return@launch
            val today = LocalDate.now()

            // Comprueba si la rutina se puede completar hoy
            if (!currentRoutine.isConcluded && currentRoutine.canBeCompletedToday) {
                val updatedRoutine = currentRoutine.copy(
                    timesDone = currentRoutine.timesDone + 1,
                    lastCompletedDate = today // Guarda la fecha de hoy
                )
                repository.updateRoutine(updatedRoutine)
            }
        }
    }

}




