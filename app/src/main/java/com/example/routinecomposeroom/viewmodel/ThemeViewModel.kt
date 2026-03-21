package com.example.routinecomposeroom.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode {
    LIGHT, DARK
}

@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val themeKey = stringPreferencesKey("theme_mode")

    var themeState = mutableStateOf(ThemeMode.LIGHT)
        private set

    init {
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences ->
                    ThemeMode.valueOf(preferences[themeKey] ?: ThemeMode.LIGHT.name)
                }
                .collect { theme ->
                    themeState.value = theme
                }
        }
    }

    fun changeTheme(mode: ThemeMode) {
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[themeKey] = mode.name
            }
        }
    }
}
