package com.alex.aerokit.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.alex.aerokit.data.LanguagePreference
import com.alex.aerokit.ui.screens.ConversionScreen
import com.alex.aerokit.ui.screens.SettingsScreen
import com.alex.aerokit.ui.screens.ToolsScreen
import com.alex.aerokit.ui.theme.*

@Composable
fun AeroKitApp() {
    val context = LocalContext.current

    // Only one instance per app run
    val languagePreference = remember { LanguagePreference(context) }
    val savedLanguage by languagePreference.languageFlow.collectAsState(initial = AppLanguage.ENGLISH)

    // Only one ThemeController, not recreated!
    val themeController = remember { ThemeController(savedLanguage, languagePreference) }

    // Keep the controller.language in sync
    LaunchedEffect(savedLanguage) {
        if (themeController.language != savedLanguage) {
            // Use the update function to guarantee DataStore stays in sync if changed elsewhere
            themeController.updateLanguage(savedLanguage)
        }
    }

    CompositionLocalProvider(LocalAppLanguage provides themeController.language) {
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
                        0 -> ToolsScreen(themeController)
                        1 -> ConversionScreen(themeController)
                        2 -> SettingsScreen(themeController)
                    }
                }
            }
        }
    }
}

