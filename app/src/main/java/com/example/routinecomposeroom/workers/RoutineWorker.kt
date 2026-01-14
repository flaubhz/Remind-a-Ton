package com.example.routinecomposeroom.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.routinecomposeroom.view.MainActivity
import com.example.routinecomposeroom.R
import com.example.routinecomposeroom.data.utils.calculateNextExecution
import com.example.routinecomposeroom.data.utils.isConcluded
import com.example.routinecomposeroom.repository.RoutineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class RoutineWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RoutineRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val routineId = inputData.getInt("ROUTINE_ID", -1)
        if (routineId == -1) return Result.failure()

        // Usamos try-catch por si la rutina fue borrada justo antes de ejecutarse
        val routine = try {
            repository.getRoutineById(routineId).first()
        } catch (e: Exception) {
            null
        } ?: return Result.success() // Si no existe, terminamos con éxito para no reintentar

        if (routine.isConcluded) return Result.success()

        showNotification(routine.name, "¡Es hora de tu rutina! Toca para ver tareas.", routineId)

        val nextDate = routine.calculateNextExecution()

        if (nextDate != null) {
            val now = LocalDateTime.now()
            val delay = Duration.between(now, nextDate).toMillis()

            if (delay > 0) {
                val nextWork = OneTimeWorkRequestBuilder<RoutineWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf("ROUTINE_ID" to routineId))
                    .addTag("routine_$routineId") // <--- ¡CRÍTICO! Mantiene la cadena identificable
                    .build()

                // Usamos applicationContext para evitar fugas de memoria, aunque context funciona
                WorkManager.getInstance(applicationContext).enqueue(nextWork)
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, desc: String, routineId: Int) {
        val channelId = "routine_channel_id"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Rutinas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos para tus rutinas programadas"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ROUTINE_ID_EXTRA", routineId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            routineId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(routineId, notification)
    }
}
