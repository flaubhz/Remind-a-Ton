package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    routineId: Int,
    viewModel: RoutineDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    isReadOnly: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(routineId) {
        viewModel.loadRoutineAndTasks(routineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.routine?.name ?: "Cargando...") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Volver")
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
    val currentRoutine = state.routine!!

    // ---  Validación de tiempo para botón ---
    val now = LocalTime.now()
    val today = LocalDate.now()

    // 1. Si se llega a la fecha de la rutina
    val isDateReached = !today.isBefore(currentRoutine.startDate)

    // 2. Si se llega a la hora de la rutina
    val isTimeReached = !now.isBefore(currentRoutine.startHour)

    // El botón solo se muestra si no hemos completado la rutina y además fecha y hora son las correctas
    val canCompleteNow = currentRoutine.canBeCompletedToday && isDateReached && isTimeReached

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tarjeta de información de la rutina
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
                Text("Progreso: ${currentRoutine.timesDone} / ${currentRoutine.totalTimes}")
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


        when {
            currentRoutine.isConcluded -> {
                // Caso: Meta total alcanzada
                StatusBox(Color(0xFF4CAF50), "¡Rutina Completada Totalmente!")
            }
            canCompleteNow -> {
                // Caso: Toca hacerla ahora
                Button(
                    onClick = onFinishButtonClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Finalizar rutina por hoy")
                }
            }
            !isDateReached -> {
                // Caso: Aún no es el día de inicio
                StatusBox(
                    MaterialTheme.colorScheme.surfaceVariant,
                    "Disponible a partir del ${currentRoutine.startDate}"
                )
            }
            !isTimeReached -> {
                // Caso: Es el día pero aún no es la hora
                StatusBox(
                    MaterialTheme.colorScheme.surfaceVariant,
                    "Disponible hoy a las ${currentRoutine.startHour}"
                )
            }
            else -> {
                // Caso: Ya se completó hoy (canBeCompletedToday es false)
                StatusBox(
                    MaterialTheme.colorScheme.secondaryContainer,
                    "¡Ya has cumplido por hoy!"
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = onConfirmDialogDismiss,
            title = { Text("Confirmar") },
            text = { Text("¿Deseas marcar la rutina como completada por hoy?") },
            confirmButton = {
                TextButton(onClick = onFinishRoutineConfirm) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = onConfirmDialogDismiss) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun StatusBox(backgroundColor: Color, message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, fontWeight = FontWeight.Bold)
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
                Text("${task.time} min", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}