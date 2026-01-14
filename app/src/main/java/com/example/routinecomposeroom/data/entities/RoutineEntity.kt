package com.example.routinecomposeroom.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.routinecomposeroom.data.database.Frequency
import java.time.LocalDate
import java.time.LocalTime

@Entity (tableName = "routines")
data class RoutineEntity(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val startHour: LocalTime,
    val frequency: Frequency,
    val totalTimes: Int,
    val timesDone: Int = 0,
    val lastCompletedDate: LocalDate? = null
)
{
    val canBeCompletedToday: Boolean
        get() = lastCompletedDate == null || lastCompletedDate.isBefore(LocalDate.now())
}
