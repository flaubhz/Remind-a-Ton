package com.example.routinecomposeroom.view.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.routinecomposeroom.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun TopBar(){
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.remind_a_ton), fontWeight = FontWeight.Bold) },
        modifier = Modifier.shadow(4.dp)
    )
}
