package com.example.routinecomposeroom.data.utils

import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.RoutineEntity
import java.time.Duration
import java.time.LocalDateTime


val RoutineEntity.isConcluded: Boolean
    get() = this.totalTimes > 0 && this.timesDone >= this.totalTimes


fun RoutineEntity.calculateNextExecution(): LocalDateTime? {


    if (this.isConcluded) return null

    val now = LocalDateTime.now()

    var nextRun = LocalDateTime.of(this.startDate, this.startHour)


    if (nextRun.isAfter(now)) {
        return nextRun
    }


    while (!nextRun.isAfter(now)) {
        nextRun = when (this.frequency) {
            Frequency.DAILY -> nextRun.plusDays(1)
            Frequency.WEEKLY -> nextRun.plusWeeks(1)
            Frequency.MONTHLY -> nextRun.plusMonths(1)
        }
    }
    return nextRun
}


fun RoutineEntity.getStatusMessage(): String {
    if (this.isConcluded) return "✅ ¡Meta alcanzada!"


    val nextRun = this.calculateNextExecution() ?: return "Sin fecha pendiente"

    val now = LocalDateTime.now()
    val duration = Duration.between(now, nextRun)

    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60

    return when {
        days > 1 -> "Faltan $days días"
        days == 1L -> "Falta 1 día"
        hours > 0 -> "Faltan ${hours}h ${minutes}m"
        minutes > 0 -> "Faltan $minutes min"
        else -> "¡Es ahora!"
    }
}
