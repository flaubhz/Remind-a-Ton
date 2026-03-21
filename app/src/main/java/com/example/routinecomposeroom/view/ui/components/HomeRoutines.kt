package com.example.routinecomposeroom.view.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.routinecomposeroom.view.ui.screens.RoutineCard
import com.example.routinecomposeroom.data.entities.RoutineEntity





@Composable
fun RoutineBox(
    modifier: Modifier = Modifier,
    upcomingRoutines: List<RoutineEntity>,
    onRoutineClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(upcomingRoutines) { routine ->
            RoutineCard(routine = routine) {
                onRoutineClick(routine.id)
            }
        }
    }
}


@Composable
fun RoutineBoxEmpty (modifier: Modifier, contentAlignment: Alignment){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No hay rutinas próximas",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}