package com.example.routinecomposeroom.view.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.data.entities.TaskEntity
import com.example.routinecomposeroom.viewmodel.HomeViewModel
import com.example.routinecomposeroom.viewmodel.RoutineDetailsViewModel


import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(

    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    // Estados para controlar los diálogos
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoutineForEdit by remember { mutableStateOf<RoutineEntity?>(null) }

    val allRoutines by viewModel.allRoutines.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Rutinas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onNavigateToHome
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Rutinas") },
                    label = { Text("Rutinas") },
                    selected = true,
                    onClick = { /* Ya estamos aquí */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Opciones") },
                    label = { Text("Opciones") },
                    selected = false,
                    onClick = onNavigateToOptions
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Rutina")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (allRoutines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No tienes rutinas creadas", style = MaterialTheme.typography.titleMedium)
                        Text("Pulsa el botón + para empezar", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allRoutines) { routine ->
                        RoutineListItem(
                            routine = routine,
                            onClick = {
                                // Al pulsar, guardamos la rutina y mostramos el modal de edición
                                selectedRoutineForEdit = routine
                                showEditDialog = true
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // Diálogo para AÑADIR (sin cambios)
        if (showAddDialog) {
            AddRoutineDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { newRoutine ->
                    viewModel.addRoutine(newRoutine)
                    showAddDialog = false
                }
            )
        }

        // Diálogo para EDITAR
        if (showEditDialog && selectedRoutineForEdit != null) {
            // ...
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
}

// --- Componente para cada tarjeta de la lista ---
@Composable
fun RoutineListItem(routine: RoutineEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = routine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                AssistChip(
                    onClick = { onClick() },
                    label = { Text(routine.frequency.frequencyDisplay) },
                    modifier = Modifier.height(24.dp)
                )
            }
            if (routine.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = routine.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}

// --- Diálogo para AÑADIR (sin cambios) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (RoutineEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startHour by remember { mutableStateOf(LocalTime.now()) }
    var frequency by remember { mutableStateOf(Frequency.DAILY) }
    var durationText by remember { mutableStateOf("1") }
    var expandedFrequency by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            startDate = LocalDate.of(year, month + 1, dayOfMonth)
        }, startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
    )
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute -> startHour = LocalTime.of(hourOfDay, minute) },
        startHour.hour, startHour.minute, true
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Nueva Rutina",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                // ... (El resto del formulario de añadir sigue igual)
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), onValueChange = {},
                        label = { Text("Inicio") }, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, "Fecha", Modifier.clickable { datePickerDialog.show() }) },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { datePickerDialog.show() },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    OutlinedTextField(
                        value = startHour.format(DateTimeFormatter.ofPattern("HH:mm")), onValueChange = {},
                        label = { Text("Hora") }, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.Notifications, "Hora", Modifier.clickable { timePickerDialog.show() }) },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { timePickerDialog.show() },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = frequency.frequencyDisplay, onValueChange = {},
                        label = { Text("Frecuencia") }, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Desplegar", Modifier.clickable { expandedFrequency = true }) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFrequency = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    DropdownMenu(expanded = expandedFrequency, onDismissRequest = { expandedFrequency = false }) {
                        Frequency.entries.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq.frequencyDisplay) },
                                onClick = {
                                    frequency = freq
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = durationText, onValueChange = { if (it.all { char -> char.isDigit() }) durationText = it },
                    label = { Text("Meta (veces totales)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val durationInt = durationText.toIntOrNull() ?: 0
                                val newRoutine = RoutineEntity(
                                    name = name, description = description, startDate = startDate,
                                    startHour = startHour, frequency = frequency, totalTimes = durationInt, timesDone = 0
                                )
                                onConfirm(newRoutine)
                            }
                        },
                        enabled = name.isNotBlank()
                    ) { Text("Guardar") }
                }
            }
        }
    }
}

// --- Diálogo para EDITAR una rutina (con la sección de TAREAS) ---
@Composable
fun EditRoutineDialog(
    routineId: Int,
    viewModel: RoutineDetailsViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    // Estados para el modal de TAREAS
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }

    LaunchedEffect(routineId) {
        viewModel.loadRoutine(routineId)
    }

    val routine by viewModel.routine.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var name by remember(routine) { mutableStateOf(routine?.name ?: "") }
    var description by remember(routine) { mutableStateOf(routine?.description ?: "") }
    var startDate by remember(routine) { mutableStateOf(routine?.startDate ?: LocalDate.now()) }
    var startHour by remember(routine) { mutableStateOf(routine?.startHour ?: LocalTime.now()) }
    var frequency by remember(routine) { mutableStateOf(routine?.frequency ?: Frequency.DAILY) }
    var totalTimesText by remember(routine) { mutableStateOf(routine?.totalTimes?.toString() ?: "1") }
    var expandedFrequency by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val datePickerDialog = DatePickerDialog(context, { _, year: Int, month: Int, dayOfMonth: Int -> startDate = LocalDate.of(year, month + 1, dayOfMonth) }, startDate.year, startDate.monthValue - 1, startDate.dayOfMonth)
    val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute -> startHour = LocalTime.of(hourOfDay, minute) }, startHour.hour, startHour.minute, true)

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            if (routine == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Editar Rutina", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                    // FORMULARIO DE EDICIÓN DE RUTINA
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), onValueChange = {}, label = { Text("Inicio") }, readOnly = true, trailingIcon = { Icon(Icons.Default.DateRange, "Fecha", Modifier.clickable { datePickerDialog.show() }) }, modifier = Modifier
                            .weight(1f)
                            .clickable { datePickerDialog.show() }, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline))
                        OutlinedTextField(value = startHour.format(DateTimeFormatter.ofPattern("HH:mm")), onValueChange = {}, label = { Text("Hora") }, readOnly = true, trailingIcon = { Icon(Icons.Default.Notifications, "Hora", Modifier.clickable { timePickerDialog.show() }) }, modifier = Modifier
                            .weight(1f)
                            .clickable { timePickerDialog.show() }, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline))
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = frequency.frequencyDisplay, onValueChange = {}, label = { Text("Frecuencia") }, readOnly = true, trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Desplegar", Modifier.clickable { expandedFrequency = true }) }, modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFrequency = true }, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline))
                        DropdownMenu(expanded = expandedFrequency, onDismissRequest = { expandedFrequency = false }) {
                            Frequency.entries.forEach { freq ->
                                DropdownMenuItem(text = { Text(freq.frequencyDisplay) }, onClick = { frequency = freq; expandedFrequency = false })
                            }
                        }
                    }
                    OutlinedTextField(value = totalTimesText, onValueChange = { if (it.all { char -> char.isDigit() }) totalTimesText = it }, label = { Text("Meta (veces totales)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                    // SECCIÓN DE GESTIÓN DE TAREAS
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tareas", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { taskToEdit = null; showTaskDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
                        }
                    }
                    if (tasks.isEmpty()) {
                        Text("No hay tareas. Pulsa + para añadir una.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tasks) { task ->
                                TaskChip(task = task, onClick = { taskToEdit = task; showTaskDialog = true })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // BOTONES DE ACCIÓN DE RUTINA
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.updateRoutineDetails(
                                newName = name,
                                newDescription = description,
                                newStartDate = startDate,
                                newStartHour = startHour,
                                newFrequency = frequency,
                                newTotalTimes = totalTimesText.toIntOrNull() ?: 0
                            )
                            onDismiss()
                        }) {
                            Text("Guardar Cambios")
                        }

                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Rutina",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Lógica para mostrar el diálogo de TAREAS
    if (showTaskDialog) {
        AddOrEditTaskDialog(
            task = taskToEdit,
            onDismiss = { showTaskDialog = false },
            onConfirm = { taskName, taskDesc, taskTime ->
                // Ahora pasamos todos los datos que recibimos del diálogo
                viewModel.addTask(taskName, taskDesc, taskTime)
                showTaskDialog = false },
            onUpdate = { task, newName, newDesc, newTime -> viewModel.updateTask(task, newName, newDesc, newTime); showTaskDialog = false },
            onDelete = { task -> viewModel.deleteTask(task); showTaskDialog = false }
        )
    }
}

// --- Componente para las tarjetas de TAREA ---
@Composable
fun TaskChip(task: TaskEntity, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(task.name) },
        trailingIcon = { Text("${task.time}m") },
        shape = CircleShape
    )
}

// --- Diálogo para AÑADIR/EDITAR TAREAS ---
@Composable
fun AddOrEditTaskDialog(
    task: TaskEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit,
    onUpdate: (TaskEntity, String, String, Int) -> Unit,
    onDelete: (TaskEntity) -> Unit
) {
    val isEditing = task != null
    var name by remember { mutableStateOf(task?.name ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var timeText by remember { mutableStateOf(task?.time?.toString() ?: "5") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = if (isEditing) "Editar Tarea" else "Nueva Tarea", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") })
                OutlinedTextField(value = timeText, onValueChange = { if (it.all(Char::isDigit)) timeText = it }, label = { Text("Duración (minutos)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isEditing) Arrangement.SpaceBetween else Arrangement.End) {
                    if (isEditing) {
                        IconButton(onClick = { onDelete(task) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    Row {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Button(
                            onClick = {
                                val time = timeText.toIntOrNull() ?: 0
                                if (isEditing) {
                                    onUpdate(task, name, description, time)
                                } else {
                                    onConfirm(name, description, time)
                                }
                            },
                            enabled = name.isNotBlank()
                        ) {
                            Text(if (isEditing) "Guardar" else "Crear")
                        }
                    }
                }
            }
        }
    }
}