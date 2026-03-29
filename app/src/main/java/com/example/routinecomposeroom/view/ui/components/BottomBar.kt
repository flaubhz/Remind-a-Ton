package com.example.routinecomposeroom.view.ui.components

import androidx.compose.material.icons.Icons // Corregido el punto antes de Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.routinecomposeroom.R

@Composable
fun BottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToAllRoutines: () -> Unit,
    onNavigateToOptions: () -> Unit,
    route: String // Cambiado a minúscula por convención de nombres
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.inicio)) },
            label = { Text(stringResource(R.string.inicio)) },
            selected = route == "Home", // Simplificado
            onClick = onNavigateToHome
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.rutinas)) },
            label = { Text(stringResource(R.string.rutinas)) },
            selected = route == "AllRoutines", // Simplificado
            onClick = onNavigateToAllRoutines
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.opciones)) },
            label = { Text(stringResource(R.string.opciones)) },
            selected = route == "Options", // Simplificado
            onClick = onNavigateToOptions
        )
    }
}




