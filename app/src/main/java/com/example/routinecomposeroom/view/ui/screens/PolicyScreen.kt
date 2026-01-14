package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Política de Privacidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        // La columna principal con scroll para diseño responsivo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {

            // Sección 1: Aviso de Privacidad
            PolicySection(
                title = "Aviso de Privacidad",
                body = "La aplicación Remind-A-Ton se despliega en su dispositivo de manera que el control de sus rutinas no será compartido con ningún tercero."
            )

            HorizontalDivider()

            // Sección 2: Uso Responsable
            PolicySection(
                title = "Uso Responsable",
                body = "Apelamos a un uso responsable de la aplicación por parte del usuario. En caso de un uso irresponsable o modificación que altere el correcto funcionamiento de la app, no será posible el soporte ni la recuperación de datos."
            )

            HorizontalDivider()

            // Sección 3: Contacto
            PolicySection(
                title = "Contacto y Soporte",
                body = "En caso de errores experimentados con la aplicación por favor consulte al siguiente correo: soporteRemind@ton.com"
            )
        }
    }
}

//Componente reutliizable para los 3 espacios de las secciones
@Composable
fun PolicySection(
    title: String,
    body: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )


        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Justify,
            lineHeight = 24.sp
        )
    }
}



