package com.example.routinecomposeroom

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp // 1. Activar Hilt en la app para poder inyectar dependencias
class RoutineApp : Application(), Configuration.Provider { // 2. Hereda de Application e implementa Provider

    // 3. Inyectar la fábrica de Workers de Hilt
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // 4. Configurar WorkManager para usar esa fábrica
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
