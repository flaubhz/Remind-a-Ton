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
import com.example.routinecomposeroom.data.utils.isConcluded
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    val allRoutines: StateFlow<List<RoutineEntity>> = repository.allRoutines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val upcomingRoutines: StateFlow<List<RoutineEntity>> = allRoutines
        .map { list ->
            list
                .filter { !it.isConcluded }
                .sortedBy { routine ->
                    routine.calculateNextExecution() ?: LocalDateTime.MAX
                }
                .take(6)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            val newRoutineId = repository.insertRoutine(routine)
            scheduleFirstNotification(routine, newRoutineId.toInt())
        }
    }

    private fun scheduleFirstNotification(routine: RoutineEntity, routineId: Int) {
        val firstExecution = routine.calculateNextExecution()
        if (firstExecution != null) {
            val now = LocalDateTime.now()
            val delay = Duration.between(now, firstExecution).toMillis()

            if (delay > 0) {
                val workRequest = OneTimeWorkRequestBuilder<RoutineWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf("ROUTINE_ID" to routineId))
                    .addTag("routine_$routineId")
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }

    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            WorkManager.getInstance(context).cancelAllWorkByTag("routine_${routine.id}")
            repository.deleteRoutine(routine)
        }
    }
}

