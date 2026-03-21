package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.viewmodel.HomeViewModel
import com.example.routinecomposeroom.view.ui.components.routinesScreen.AddRoutineDialog
import com.example.routinecomposeroom.view.ui.components.routinesScreen.EditRoutineDialog
import com.example.routinecomposeroom.view.ui.components.routinesScreen.EmptyRoutinesBox
import com.example.routinecomposeroom.view.ui.components.routinesScreen.RoutinesBox
import com.example.routinecomposeroom.view.ui.components.BottomBar
import com.example.routinecomposeroom.view.ui.components.TopBar


@Composable
fun RoutinesScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoutineForEdit by remember { mutableStateOf<RoutineEntity?>(null) }
    val allRoutines by viewModel.allRoutines.collectAsState()

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToAllRoutines = { },
                onNavigateToOptions = onNavigateToOptions,
                "AllRoutines"
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                // Sustituimos el Icon por un texto simple para evitar errores de importación
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (allRoutines.isEmpty()) {
                EmptyRoutinesBox(modifier = Modifier.align(Alignment.Center))
            } else {
                RoutinesBox(
                    routines = allRoutines,
                    onRoutineClick = { routine ->
                        selectedRoutineForEdit = routine
                        showEditDialog = true
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newRoutine ->
                viewModel.addRoutine(newRoutine)
                showAddDialog = false
            }
        )
    }

    if (showEditDialog && selectedRoutineForEdit != null) {
        key(selectedRoutineForEdit!!.id) {
            EditRoutineDialog(
                routineId = selectedRoutineForEdit!!.id,
                onDismiss = { showEditDialog = false },
                onDelete = {
                    viewModel.deleteRoutine(selectedRoutineForEdit!!)
                    showEditDialog = false
                }
            )
        }
    }
}
