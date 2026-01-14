package com.example.routinecomposeroom.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.utils.calculateNextExecution
import com.example.routinecomposeroom.repository.RoutineRepository
import com.example.routinecomposeroom.workers.RoutineWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RoutineRepository,
    @ApplicationContext private val context: Context // Necesario para WorkManager (aunque Hilt puede inyectar WorkManager directamente si lo configuras, usaremos context aquí por simplicidad)
) : ViewModel() {

    // --- LECTURA DE DATOS ---

    // Obtenemos todas las rutinas y filtramos las 3 más urgentes
    val upcomingRoutines: StateFlow<List<RoutineEntity>> = repository.allRoutines
        .map { list ->
            list.sortedBy { routine ->
                // Usamos la función de extensión limpia que creamos en RoutineCalc.kt
                // Si devuelve null (rutina acabada), la mandamos al final de la lista
                routine.calculateNextExecution() ?: LocalDateTime.MAX
            }.take(6)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- NUEVO: Exponemos TODAS las rutinas para la pantalla AllRoutinesScreen ---
    val allRoutines: StateFlow<List<RoutineEntity>> = repository.allRoutines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )



    // --- ACCIONES DEL USUARIO ---

    fun addRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            // 1. Guardar en la Base de Datos y obtener el nuevo ID
            val newRoutineId = repository.insertRoutine(routine)

            // 2. Programar la PRIMERA notificación
            scheduleFirstNotification(routine, newRoutineId.toInt())
        }
    }

    private fun scheduleFirstNotification(routine: RoutineEntity, routineId: Int) {
        // Calculamos cuándo debe sonar por primera vez
        val firstExecution = routine.calculateNextExecution()

        if (firstExecution != null) {
            val now = LocalDateTime.now()
            val delay = Duration.between(now, firstExecution).toMillis()

            if (delay > 0) {
                // Creamos el trabajo para el Worker
                val workRequest = OneTimeWorkRequestBuilder<RoutineWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf("ROUTINE_ID" to routineId))
                    .addTag("routine_$routineId") // Etiqueta útil para cancelar si borras la rutina
                    .build()

                // Encolamos el trabajo
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }

    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            // 1. Cancelar las notificaciones pendientes
            WorkManager.getInstance(context).cancelAllWorkByTag("routine_${routine.id}")

            // 2. Borrar de la BBDD
            repository.deleteRoutine(routine)
        }
    }
}
