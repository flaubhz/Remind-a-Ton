package com.example.routinecomposeroom.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.routinecomposeroom.data.dao.RoutineDao
import com.example.routinecomposeroom.data.dao.TaskDao
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.entities.TaskEntity
import androidx.room.AutoMigration

@Database(
    entities = [RoutineEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = true,

)
@TypeConverters(Converters::class)
abstract class ProjectDataBase: RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun taskDao(): TaskDao
}