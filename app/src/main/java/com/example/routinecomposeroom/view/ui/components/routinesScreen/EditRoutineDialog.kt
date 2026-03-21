package com.example.routinecomposeroom.view.ui.components.routinesScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.TaskEntity
import com.example.routinecomposeroom.viewmodel.RoutineDetailsViewModel
import java.time.LocalDate
import java.time.LocalTime
import com.example.routinecomposeroom.view.ui.components.routinesScreen.DateSelector
import com.example.routinecomposeroom.view.ui.components.routinesScreen.FrequencyDropdown
import com.example.routinecomposeroom.view.ui.components.routinesScreen.LabeledTextField
import com.example.routinecomposeroom.view.ui.components.routinesScreen.TimeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoutineDialog(
    routineId: Int,
    viewModel: RoutineDetailsViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {

    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }

    LaunchedEffect(routineId) {
        viewModel.loadRoutineAndTasks(routineId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.routine != null) {
                EditRoutineForm(
                    initialState = uiState,
                    onDismiss = onDismiss,
                    onDelete = onDelete,
                    onUpdate = { name, description, startDate, startHour, frequency, totalTimes ->
                        viewModel.updateRoutineDetails(
                            name,
                            description,
                            startDate,
                            startHour,
                            frequency,
                            totalTimes
                        )
                        onDismiss()
                    },
                    onShowTaskDialog = { task ->
                        taskToEdit = task
                        showTaskDialog = true
                    }
                )
            }
        }
    }

    if (showTaskDialog) {
        AddOrEditTaskDialog(
            task = taskToEdit,
            onDismiss = { showTaskDialog = false },
            onConfirm = { taskName, taskDesc, taskTime ->
                viewModel.addTask(taskName, taskDesc, taskTime)
                showTaskDialog = false
            },
            onUpdate = { task, newName, newDesc, newTime ->
                viewModel.updateTask(task, newName, newDesc, newTime)
                showTaskDialog = false
            },
            onDelete = { task ->
                viewModel.deleteTask(task)
                showTaskDialog = false
            }
        )
    }
}

@Composable
private fun EditRoutineForm(
    initialState: com.example.routinecomposeroom.viewmodel.RoutineDetailsState,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String, String, LocalDate, LocalTime, Frequency, Int) -> Unit,
    onShowTaskDialog: (TaskEntity?) -> Unit
) {
    var name by remember { mutableStateOf(initialState.routine!!.name) }
    var description by remember { mutableStateOf(initialState.routine!!.description) }
    var startDate by remember { mutableStateOf(initialState.routine!!.startDate) }
    var startHour by remember { mutableStateOf(initialState.routine!!.startHour) }
    var frequency by remember { mutableStateOf(initialState.routine!!.frequency) }
    var totalTimesText by remember { mutableStateOf(initialState.routine!!.totalTimes.toString()) }
    var expandedFrequency by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Editar Rutina", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        LabeledTextField(value = name, label = "Nombre", onValueChange = { name = it })
        LabeledTextField(value = description, label = "Descripción", onValueChange = { description = it }, singleLine = false)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateSelector(date = startDate, onDateSelected = { startDate = it }, modifier = Modifier.weight(1f))
            TimeSelector(time = startHour, onTimeSelected = { startHour = it }, modifier = Modifier.weight(1f))
        }

        FrequencyDropdown(
            selected = frequency,
            expanded = expandedFrequency,
            onExpandChange = { expandedFrequency = it },
            onSelect = { frequency = it; expandedFrequency = false }
        )

        LabeledTextField(
            value = totalTimesText,
            label = "Meta (veces)",
            onValueChange = { totalTimesText = it },
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tareas", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { onShowTaskDialog(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
            }
        }

        if (initialState.tasks.isEmpty()) {
            Text("No hay tareas. Pulsa + para añadir una.", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(initialState.tasks, key = { it.id }) { task ->
                    TaskChip(task = task, onClick = { onShowTaskDialog(task) })
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar Rutina", tint = MaterialTheme.colorScheme.error)
            }
            Row {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(onClick = {
                    onUpdate(
                        name,
                        description,
                        startDate,
                        startHour,
                        frequency,
                        totalTimesText.toIntOrNull() ?: 0
                    )
                }) { Text("Guardar") }
            }
        }
    }
}

@Composable
fun TaskChip(task: TaskEntity, onClick: () -> Unit) {
    InputChip(
        selected = false,
        onClick = onClick,
        label = { Text(task.name) },
        trailingIcon = {
            Icon(
                Icons.Default.Edit,
                "Editar Tarea",
                modifier = Modifier.size(InputChipDefaults.IconSize)
            )
        }
    )
}

@Composable
fun AddOrEditTaskDialog(
    task: TaskEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit,
    onUpdate: (TaskEntity, String, String, Int) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    val isEditing = task != null
    var taskName by remember(task) { mutableStateOf(task?.name ?: "") }
    var taskDesc by remember(task) { mutableStateOf(task?.description ?: "") }
    var taskTime by remember(task) { mutableStateOf(task?.time?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Tarea" else "Nueva Tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LabeledTextField(value = taskName, label = "Nombre", onValueChange = { taskName = it })
                LabeledTextField(value = taskDesc, label = "Descripción (opcional)", onValueChange = { taskDesc = it }, singleLine = false)
                LabeledTextField(
                    value = taskTime,
                    label = "Duración (minutos)",
                    onValueChange = { taskTime = it },
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isEditing) {
                        onUpdate(task!!, taskName, taskDesc, taskTime.toIntOrNull() ?: 0)
                    } else {
                        onConfirm(taskName, taskDesc, taskTime.toIntOrNull() ?: 0)
                    }
                },
                enabled = taskName.isNotBlank()
            ) { Text(if (isEditing) "Guardar" else "Añadir") }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditing) {
                    IconButton(onClick = { onDelete(task!!) }) {
                        Icon(Icons.Default.Delete, "Borrar Tarea", tint = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        }
    )
}



