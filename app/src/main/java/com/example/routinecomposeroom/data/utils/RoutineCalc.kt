package com.example.routinecomposeroom.data.utils

import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.RoutineEntity
import java.time.Duration
import java.time.LocalDateTime


val RoutineEntity.isConcluded: Boolean
    get() = this.totalTimes > 0 && this.timesDone >= this.totalTimes

// 2. CÁLCULO DE LA PRÓXIMA NOTIFICACIÓN
// Esta función la usará tu WorkManager para saber cuándo despertar la próxima vez.
fun RoutineEntity.calculateNextExecution(): LocalDateTime? {

    // Si ya terminó según la lógica de arriba, no hay próxima fecha (devuelve null)
    if (this.isConcluded) return null

    val now = LocalDateTime.now()
    // Reconstruimos la fecha de inicio original
    var nextRun = LocalDateTime.of(this.startDate, this.startHour)

    // Si la fecha original es en el futuro, esa es la respuesta.
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

// 3. TEXTO PARA LA INTERFAZ DE USUARIO
// Esta función la usará tu HomeScreen para mostrar "Faltan 2 días"
fun RoutineEntity.getStatusMessage(): String {
    if (this.isConcluded) return "✅ ¡Meta alcanzada!"

    // Calculamos la siguiente fecha.
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
