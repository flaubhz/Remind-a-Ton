package com.example.routinecomposeroom.view.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.routinecomposeroom.viewmodel.AddRoutineViewModel
import com.example.routinecomposeroom.viewmodel.HomeViewModel
import com.example.routinecomposeroom.view.ui.components.routinesScreen.DateSelector
import com.example.routinecomposeroom.view.ui.components.routinesScreen.FrequencyDropdown
import com.example.routinecomposeroom.view.ui.components.routinesScreen.LabeledTextField
import com.example.routinecomposeroom.view.ui.components.routinesScreen.TimeSelector


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    addRoutineViewModel: AddRoutineViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by addRoutineViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledTextField(
                value = state.name,
                label = "Nombre de la rutina",
                onValueChange = addRoutineViewModel::onNameChange
            )

            LabeledTextField(
                value = state.description,
                label = "Descripción (opcional)",
                onValueChange = addRoutineViewModel::onDescriptionChange,
                singleLine = false
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateSelector(
                    date = state.startDate,
                    onDateSelected = addRoutineViewModel::onDateChange,
                    modifier = Modifier.weight(1f)
                )
                TimeSelector(
                    time = state.startHour,
                    onTimeSelected = addRoutineViewModel::onHourChange,
                    modifier = Modifier.weight(1f)
                )
            }

            FrequencyDropdown(
                selected = state.frequency,
                expanded = state.expandedFrequency,
                onExpandChange = addRoutineViewModel::onToggleFrequencyMenu,
                onSelect = addRoutineViewModel::onFrequencyChange
            )

            LabeledTextField(
                value = state.totalTimes,
                label = "Meta (veces totales)",
                onValueChange = addRoutineViewModel::onTotalTimesChange,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    addRoutineViewModel.toRoutineEntity()?.let { newRoutine ->
                        homeViewModel.addRoutine(newRoutine)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.name.isNotBlank() && (state.totalTimes.toIntOrNull() ?: 0) > 0
            ) {
                Text("Crear Rutina")
            }
        }
    }
}

