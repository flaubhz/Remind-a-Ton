
package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.utils.getStatusMessage
import com.example.routinecomposeroom.view.ui.components.BottomBar
import com.example.routinecomposeroom.view.ui.components.RoutineBox
import com.example.routinecomposeroom.view.ui.components.RoutineBoxEmpty
import com.example.routinecomposeroom.view.ui.components.TopBar
import com.example.routinecomposeroom.viewmodel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAllRoutines: () -> Unit,
    onNavigateToOptions: () -> Unit,
    onRoutineClick: (Int) -> Unit
) {
    val upcomingRoutines by viewModel.upcomingRoutines.collectAsState()
    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(
                onNavigateToHome = {},
                onNavigateToAllRoutines = onNavigateToAllRoutines,
                onNavigateToOptions = onNavigateToOptions,
                "Home"
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Próximas Rutinas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            if (upcomingRoutines.isEmpty()) {
                RoutineBoxEmpty(Modifier.fillMaxSize(),Alignment.Center)
            } else {
                RoutineBox( modifier = Modifier.fillMaxSize(), upcomingRoutines = upcomingRoutines, onRoutineClick = onRoutineClick )
                }


                }
            }
        }


@Composable
fun RoutineCard(routine: RoutineEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = routine.getStatusMessage(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
            }


            if (routine.description.isNotBlank()) {
                Text(
                    text = routine.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}
