plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.example.routinecomposeroom"
    compileSdk = 36 // Recomendación: Bajar a 34 si no usas APIs específicas de Android 15

    defaultConfig {
        applicationId = "com.example.routinecomposeroom"
        minSdk = 27
        targetSdk = 35 // Recomendación: Bajar a 34 para mayor estabilidad actual
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // Este bloque ahora funcionará gracias a que activamos el plugin arriba.
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    // --- Core y UI de Compose ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // Importa la "lista de materiales" de Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text)

    // --- Room (Base de Datos) ---
    // Unificada y sin duplicados
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.material3)
    ksp(libs.androidx.room.compiler)

    // --- DataStore (Preferencias de Tema) ---
    // Sin duplicados
    implementation(libs.androidx.datastore.preferences)

    // --- WorkManager (Notificaciones en segundo plano) ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Hilt (Inyección de Dependencias) ---
    // Sin duplicados
    implementation(libs.hilt.android)
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)

    // --- Dependencias de Test ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

