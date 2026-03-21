package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.routinecomposeroom.data.entities.TaskEntity
import com.example.routinecomposeroom.data.utils.getStatusMessage
import com.example.routinecomposeroom.data.utils.isConcluded
import com.example.routinecomposeroom.viewmodel.RoutineDetailsViewModel
import com.example.routinecomposeroom.viewmodel.RoutineDetailsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    viewModel: RoutineDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    isReadOnly: Boolean
) {

    val uiState by viewModel.uiState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.routine?.name ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.routine != null) {

            RoutineDetailContent(
                modifier = Modifier.padding(paddingValues),
                state = uiState,
                showConfirmDialog = showConfirmDialog,
                onConfirmDialogDismiss = { showConfirmDialog = false },
                onFinishButtonClick = { showConfirmDialog = true },
                onFinishRoutineConfirm = {
                    viewModel.onFinishRoutineClicked()
                    showConfirmDialog = false
                }
            )
        } else {

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Rutina no encontrada")
            }
        }
    }
}

@Composable
private fun RoutineDetailContent(
    modifier: Modifier = Modifier,
    state: RoutineDetailsState,
    showConfirmDialog: Boolean,
    onConfirmDialogDismiss: () -> Unit,
    onFinishButtonClick: () -> Unit,
    onFinishRoutineConfirm: () -> Unit
) {
    val currentRoutine = state.routine!! // We know it's not null here
    var showFinishButton by remember(currentRoutine.canBeCompletedToday) {
        mutableStateOf(currentRoutine.canBeCompletedToday)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = currentRoutine.getStatusMessage(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Progress: ${currentRoutine.timesDone} / ${currentRoutine.totalTimes}")
                if (currentRoutine.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentRoutine.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text("Tareas:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.tasks) { task ->
                TaskItemReadOnly(task = task)
            }
        }

        if (currentRoutine.isConcluded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4CAF50), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Rutina completada!", color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else if (showFinishButton) {
            Button(
                onClick = onFinishButtonClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Finalizar rutina por hoy")
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Rutina completada por hoy!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }


    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = onConfirmDialogDismiss,
            title = { Text("Confirmar Completado") },
            text = { Text(
                "¿Deseas terminar por hoy?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onFinishRoutineConfirm()
                        showFinishButton = false // Hide the button immediately
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = onConfirmDialogDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TaskItemReadOnly(task: TaskEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (task.description.isNotBlank()) {
                    Text(task.description, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (task.time > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${task.time} min", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
