package com.example.routinecomposeroom.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.entities.TaskEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface RoutineDao {

    @Insert
    suspend fun insertRoutine (routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine (routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine (routine: RoutineEntity)

    @Query ("SELECT * FROM routines")
    fun selectRoutines():Flow<List<RoutineEntity>>

    // Necesario para el Worker y para la pantalla de detalles
    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineById(id: Int): Flow<RoutineEntity>
}

