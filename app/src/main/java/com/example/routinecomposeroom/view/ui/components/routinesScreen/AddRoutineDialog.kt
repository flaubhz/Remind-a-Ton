package com.example.routinecomposeroom.view.ui.components.routinesScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.routinecomposeroom.data.entities.RoutineEntity
import com.example.routinecomposeroom.viewmodel.AddRoutineViewModel



@Composable
fun AddRoutineDialog(
    viewModel: AddRoutineViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onConfirm: (RoutineEntity) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(16.dp)) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Nueva Rutina", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)


                LabeledTextField(value = state.name, label = "Nombre de la rutina", onValueChange = viewModel::onNameChange)
                LabeledTextField(value = state.description, label = "Descripción (opcional)", onValueChange = viewModel::onDescriptionChange, singleLine = false)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DateSelector(date = state.startDate, onDateSelected = viewModel::onDateChange, modifier = Modifier.weight(1f))
                    TimeSelector(
                        time = state.startHour,
                        onTimeSelected = viewModel::onHourChange,
                        modifier = Modifier.weight(1f)
                    )
                }

                FrequencyDropdown(
                    selected = state.frequency,
                    expanded = state.expandedFrequency,
                    onExpandChange = viewModel::onToggleFrequencyMenu,
                    onSelect = viewModel::onFrequencyChange
                )

                LabeledTextField(value = state.totalTimes, label = "Meta (veces totales)", onValueChange = viewModel::onTotalTimesChange, keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(
                        onClick = {
                            viewModel.toRoutineEntity()?.let { routine ->
                                onConfirm(routine)
                            }
                        },
                        enabled = state.name.isNotBlank() && (state.totalTimes.toIntOrNull() ?: 0) > 0
                    ) {
                        Text("Crear")
                    }
                }
            }
        }
    }
}
