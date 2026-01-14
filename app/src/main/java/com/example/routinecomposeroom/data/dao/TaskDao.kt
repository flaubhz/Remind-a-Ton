package com.example.routinecomposeroom.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.routinecomposeroom.data.entities.TaskEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask (task: TaskEntity)

    @Update
    suspend fun updateTask (task: TaskEntity)

    @Delete
    suspend fun deleteTask (task: TaskEntity)

    @Query( "SELECT * FROM tasks WHERE routineId = :routineId" )
    fun getRoutineTasks(routineId: Int): Flow<List<TaskEntity>>

}