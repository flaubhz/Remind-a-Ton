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



    val allRoutines: Flow<List<RoutineEntity>> = routineDao.selectRoutines()


    fun getRoutineById(id: Int): Flow<RoutineEntity?> {
        return routineDao.getRoutineById(id)
    }


    suspend fun insertRoutine(routine: RoutineEntity): Long {
        return routineDao.insertRoutine(routine)
    }

    suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteRoutine(routine)
    }



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
