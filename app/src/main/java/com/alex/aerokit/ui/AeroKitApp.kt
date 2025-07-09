package com.alex.aerokit.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CompareArrows
import com.alex.aerokit.ui.screens.ConversionScreen
import com.alex.aerokit.ui.screens.SettingsScreen
import com.alex.aerokit.ui.screens.ToolsScreen
import com.alex.aerokit.ui.theme.ThemeController
import com.alex.aerokit.ui.theme.AppThemeMode // <-- Add this import!

@Composable
fun AeroKitApp() {
    val themeController = remember { ThemeController() }

    MaterialTheme(
        colorScheme = when (themeController.themeMode) {
            AppThemeMode.DARK -> darkColorScheme()
            AppThemeMode.LIGHT -> lightColorScheme()
            AppThemeMode.SYSTEM -> if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
        }
    ) {
        var bottomTab by remember { mutableStateOf(0) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = bottomTab == 0,
                        onClick = { bottomTab = 0 },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Tools") },
                        label = { Text("Tools") }
                    )
                    NavigationBarItem(
                        selected = bottomTab == 1,
                        onClick = { bottomTab = 1 },
                        icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Convert") },
                        label = { Text("Convert") }
                    )
                    NavigationBarItem(
                        selected = bottomTab == 2,
                        onClick = { bottomTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { padding ->
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (bottomTab) {
                    0 -> ToolsScreen()
                    1 -> ConversionScreen()
                    2 -> SettingsScreen(themeController)
                }
            }
        }
    }
}
