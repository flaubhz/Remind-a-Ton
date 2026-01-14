package com.example.routinecomposeroom.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.routinecomposeroom.data.database.Frequency
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.viewmodel.HomeViewModel

// IMPORT CORREGIDO:

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // --- ESTADOS DEL FORMULARIO ---
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Fechas y Horas por defecto: Hoy y Ahora
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startHour by remember { mutableStateOf(LocalTime.now()) }

    // Frecuencia y Duración
    var frequency by remember { mutableStateOf(Frequency.DAILY) }
    var durationText by remember { mutableStateOf("1") }
    var expandedFrequency by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // --- DIÁLOGOS (DATE & TIME PICKERS) ---
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            startDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            startHour = LocalTime.of(hourOfDay, minute)
        },
        startHour.hour, startHour.minute, true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Rutina") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. NOMBRE
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. DESCRIPCIÓN
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 3. FECHA DE INICIO
                OutlinedTextField(
                    value = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    onValueChange = {},
                    label = { Text("Inicio") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { datePickerDialog.show() },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // 4. HORA DE INICIO
                OutlinedTextField(
                    value = startHour.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = {},
                    label = { Text("Hora") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { timePickerDialog.show() }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Seleccionar hora")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { timePickerDialog.show() },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // 5. SELECTOR DE FRECUENCIA
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = frequency.frequencyDisplay,
                    onValueChange = {},
                    label = { Text("Frecuencia") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, "Desplegar", Modifier.clickable { expandedFrequency = true })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFrequency = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                DropdownMenu(
                    expanded = expandedFrequency,
                    onDismissRequest = { expandedFrequency = false }
                ) {
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

            // 6. DURACIÓN (TOTAL TIMES)
            OutlinedTextField(
                value = durationText,
                onValueChange = { if (it.all { char -> char.isDigit() }) durationText = it },
                label = { Text("Repeticiones totales (Meta)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Ej: 10 veces") }
            )

            Spacer(modifier = Modifier.weight(1f))

            // 7. BOTÓN GUARDAR
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val durationInt = durationText.toIntOrNull() ?: 0

                        val newRoutine = RoutineEntity(
                            name = name,
                            description = description,
                            startDate = startDate,
                            startHour = startHour,
                            frequency = frequency,
                            totalTimes = durationInt,
                            timesDone = 0,
                        )

                        viewModel.addRoutine(newRoutine)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Crear Rutina")
            }
        }
    }
}
