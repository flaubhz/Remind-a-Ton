package com.example.routinecomposeroom.dependency

import android.content.Context
import androidx.room.Room
import com.example.routinecomposeroom.data.dao.RoutineDao
import com.example.routinecomposeroom.data.dao.TaskDao
import com.example.routinecomposeroom.data.database.ProjectDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProjectDataBase {
        return Room.databaseBuilder(
            context,
            ProjectDataBase::class.java,
            "routine_database"
        ).build()
    }

    @Provides
    fun provideRoutineDao(db: ProjectDataBase): RoutineDao = db.routineDao()

    @Provides
    fun provideTaskDao(db: ProjectDataBase): TaskDao = db.taskDao()
}
