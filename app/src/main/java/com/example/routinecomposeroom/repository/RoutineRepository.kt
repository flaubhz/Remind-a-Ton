package com.example.routinecomposeroom.repository

import com.example.routinecomposeroom.data.dao.RoutineDao
import com.example.routinecomposeroom.data.dao.TaskDao
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao,
    private val taskDao: TaskDao
) {

    // --- SECCIÓN RUTINAS ---

    val allRoutines: Flow<List<RoutineEntity>> = routineDao.selectRoutines()

    // 1. NUEVO: Función para obtener una rutina individual (Necesaria para WorkManager)
    fun getRoutineById(id: Int): Flow<RoutineEntity?> {
        return routineDao.getRoutineById(id)
    }

    // 2. MODIFICADO: Devuelve Long (el ID generado)
    // Esto es vital para que al crear una rutina, sepamos qué ID pasarle a la primera notificación
    suspend fun insertRoutine(routine: RoutineEntity): Long {
        return routineDao.insertRoutine(routine)
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteRoutine(routine)
    }

    // --- SECCIÓN TAREAS ---

    fun getTasksForRoutine(routineId: Int): Flow<List<TaskEntity>> {

        return taskDao.getRoutineTasks(routineId)
    }

    suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }
}
