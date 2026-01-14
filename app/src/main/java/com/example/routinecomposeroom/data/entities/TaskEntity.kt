package com.example.routinecomposeroom.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity( tableName = "tasks",
    indices =  [Index(value = ["routineId"])],
    foreignKeys = [ForeignKey(
        entity = RoutineEntity::class,
        parentColumns = ["id"],
        childColumns = ["routineId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )])

data class TaskEntity(
    @PrimaryKey (autoGenerate = true) val id : Int = 0,
    val name : String,
    val description : String,
    val time : Int,
    val routineId : Int
)
