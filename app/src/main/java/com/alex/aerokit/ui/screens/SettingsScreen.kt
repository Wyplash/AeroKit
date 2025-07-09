// file: ui/screens/SettingsScreen.kt
package com.alex.aerokit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alex.aerokit.ui.theme.ThemeController
import com.alex.aerokit.ui.theme.AppThemeMode


@Composable
fun SettingsScreen(themeController: ThemeController) {
    Column(Modifier.padding(24.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        Text("Theme")
        Spacer(Modifier.height(8.dp))
        ThemeModeSelector(
            selected = themeController.themeMode,
            onSelected = { themeController.themeMode = it }
        )
    }
}

@Composable
fun ThemeModeSelector(selected: AppThemeMode, onSelected: (AppThemeMode) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        ThemeRadioButton("Light", AppThemeMode.LIGHT, selected, onSelected)
        ThemeRadioButton("Dark", AppThemeMode.DARK, selected, onSelected)
        ThemeRadioButton("Auto", AppThemeMode.SYSTEM, selected, onSelected) // <- This line is correct!
    }
}

@Composable
fun ThemeRadioButton(
    label: String,
    mode: AppThemeMode,
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        RadioButton(
            selected = (selected == mode),
            onClick = { onSelected(mode) }
        )
        Text(label)
    }
}
