
package com.example.routinecomposeroom.view.ui.components.routinesScreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.routinecomposeroom.data.database.Frequency
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun LabeledTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        singleLine = singleLine,
        modifier = modifier.fillMaxWidth()
    )
}


//Componente del modal para rutina , mostramos la fecha para elegir
@Composable
fun DateSelector(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dialog = DatePickerDialog(context, { _, y, m, d -> onDateSelected(LocalDate.of(y, m + 1, d)) }, date.year, date.monthValue - 1, date.dayOfMonth)

    OutlinedTextField(
        value = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        onValueChange = {},
        readOnly = true,
        label = { Text("Inicio") },
        trailingIcon = { Icon(Icons.Default.DateRange, "Seleccionar fecha", Modifier.clickable { dialog.show() }) },
        modifier = modifier.clickable { dialog.show() },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline
        ),
        enabled = false
    )
}

//Componente para rutina, mostramos la hora para que elija el usuario
@Composable
fun TimeSelector(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dialog = TimePickerDialog(context, { _, hour, minute -> onTimeSelected(LocalTime.of(hour, minute)) }, time.hour, time.minute, true)

    OutlinedTextField(
        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = {},
        readOnly = true,
        label = { Text("Hora") },
        trailingIcon = { Icon(Icons.Default.Notifications, "Seleccionar hora", Modifier.clickable { dialog.show() }) },
        modifier = modifier.clickable { dialog.show() },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline
        ),
        enabled = false
    )
}

@Composable
fun FrequencyDropdown(
    selected: Frequency,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSelect: (Frequency) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected.frequencyDisplay,
            onValueChange = {},
            readOnly = true,
            label = { Text("Frecuencia") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Desplegar", Modifier.clickable { onExpandChange(true) }) },
            modifier = Modifier.fillMaxWidth().clickable { onExpandChange(true) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            ),
            enabled = false
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            Frequency.entries.forEach { freq ->
                DropdownMenuItem(
                    text = { Text(freq.frequencyDisplay) },
                    onClick = { onSelect(freq) }
                )
            }
        }
    }
}
