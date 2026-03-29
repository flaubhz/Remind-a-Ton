package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.routinecomposeroom.viewmodel.ThemeMode
import com.example.routinecomposeroom.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Sustituimos el IconButton con Icon por un TextButton con la palabra "Volver"
                    TextButton(onClick = onNavigateBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                ThemeSwitch(
                    currentTheme = themeViewModel.themeState.value,
                    onThemeChange = { newTheme ->
                        themeViewModel.changeTheme(newTheme)
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSwitch(currentTheme: ThemeMode, onThemeChange: (ThemeMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (currentTheme == ThemeMode.DARK) "Activado" else "Desactivado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = currentTheme == ThemeMode.DARK,
            onCheckedChange = { isChecked ->
                val newTheme = if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT
                onThemeChange(newTheme)
            }
        )
    }
}