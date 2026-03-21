package com.example.routinecomposeroom.view.ui.components

import androidx.compose.material.icons.Icons
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
fun BottomBar (onNavigateToHome: () -> Unit,
               onNavigateToAllRoutines: () -> Unit,
               onNavigateToOptions: () -> Unit,
               Route: String){

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.inicio)) },
            label = { Text(stringResource(R.string.inicio)) },
            selected =  if (Route ==="Home") true else false,
            onClick = onNavigateToHome
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.rutinas)) },
            label = { Text(stringResource(R.string.rutinas)) },
            selected = if (Route ==="AllRoutines") true else false,
            onClick = onNavigateToAllRoutines
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.opciones) ) },
            label = { Text(stringResource(R.string.opciones)) },
            selected = if (Route ==="Options") true else false,
            onClick = onNavigateToOptions
        )
    }
}




