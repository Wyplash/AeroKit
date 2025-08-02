package com.alex.aerotool.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Transform
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alex.aerotool.data.LanguagePreference
import com.alex.aerotool.ui.screens.ConversionScreen
import com.alex.aerotool.ui.screens.SettingsScreen
import com.alex.aerotool.ui.screens.ToolsScreen
import com.alex.aerotool.ui.theme.AeroKitTheme
import com.alex.aerotool.ui.theme.AppLanguage
import com.alex.aerotool.ui.theme.AviationDarkGrey
import com.alex.aerotool.ui.theme.AviationGold
import com.alex.aerotool.ui.theme.LocalAppLanguage
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun AeroToolApp() {
    val context = LocalContext.current

    // Only one instance per app run
    val languagePreference = remember { LanguagePreference(context) }
    val savedLanguage by languagePreference.languageFlow.collectAsState(initial = AppLanguage.ENGLISH)

    // Only one ThemeController, not recreated!
    val themeController = remember { ThemeController(savedLanguage, languagePreference, context) }

    // Keep the controller.language in sync
    LaunchedEffect(savedLanguage) {
        if (themeController.language != savedLanguage) {
            // Use the update function to guarantee DataStore stays in sync if changed elsewhere
            themeController.updateLanguage(savedLanguage)
        }
    }

    // Load aircraft preferences from DataStore on app startup
    LaunchedEffect(Unit) {
        themeController.loadAircraftPrefs()
    }

    // --------- Custom abbreviations state (list) for sharing between screens ---------
    var customAbbreviations by remember { mutableStateOf(listOf<com.alex.aerotool.ui.screens.AbbreviationItem>()) }
    fun clearCustomAbbreviations() {
        customAbbreviations = emptyList()
    }

    // --------- Deep-link state for opening aircraft manager ---------
    var showAircraftManagerOnSettingsLoad by remember { mutableStateOf(false) }

    // --------- Calculator state for persistence across screens ---------
    var calculatorExpression by remember { mutableStateOf("") }
    var calculatorResult by remember { mutableStateOf("") }
    fun setCalculatorState(expr: String, res: String) {
        calculatorExpression = expr
        calculatorResult = res
    }

    CompositionLocalProvider(LocalAppLanguage provides themeController.language) {
        AeroKitTheme {
            var bottomTab by remember { mutableStateOf(0) }
            val gold = AviationGold
            val unselected = AviationDarkGrey
            Scaffold(
                modifier = Modifier.navigationBarsPadding(),
                bottomBar = {
                    Box(Modifier.height(80.dp)) {
                        NavigationBar(modifier = Modifier.fillMaxWidth()) {
                            NavigationBarItem(
                                selected = bottomTab == 0,
                                onClick = { bottomTab = 0 },
                                icon = {
                                    Icon(
                                        Icons.Outlined.Flight,
                                        contentDescription = "Tools",
                                        tint = if (bottomTab == 0) gold else unselected
                                    )
                                },
                                label = { Text("Tools") }
                            )
                            NavigationBarItem(
                                selected = bottomTab == 1,
                                onClick = { bottomTab = 1 },
                                icon = {
                                    Icon(
                                        Icons.Outlined.Transform,
                                        contentDescription = "Convert",
                                        tint = if (bottomTab == 1) gold else unselected
                                    )
                                },
                                label = { Text("Convert") }
                            )
                            NavigationBarItem(
                                selected = bottomTab == 2,
                                onClick = { bottomTab = 2 },
                                icon = {
                                    Icon(
                                        Icons.Outlined.Settings,
                                        contentDescription = "Settings",
                                        tint = if (bottomTab == 2) gold else unselected
                                    )
                                },
                                label = { Text("Settings") }
                            )
                        }
                    }
                }
            ) { padding ->
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    when (bottomTab) {
                        0 -> ToolsScreen(
                            themeController = themeController,
                            customAbbreviations = customAbbreviations,
                            setCustomAbbreviations = { customAbbreviations = it },
                            calculatorExpression = calculatorExpression,
                            calculatorResult = calculatorResult,
                            setCalculatorState = ::setCalculatorState,
                            onShowAircraftManager = {
                                bottomTab = 2
                                showAircraftManagerOnSettingsLoad = true
                            }
                        )
                        1 -> ConversionScreen(
                            themeController = themeController,
                            calculatorExpression = calculatorExpression,
                            calculatorResult = calculatorResult,
                            setCalculatorState = ::setCalculatorState
                        )
                        2 -> SettingsScreen(
                            themeController = themeController,
                            clearCustomAbbreviations = { clearCustomAbbreviations() },
                            showAircraftManagerInitially = showAircraftManagerOnSettingsLoad,
                            onAircraftManagerDismissed = {
                                showAircraftManagerOnSettingsLoad = false
                            }
                        )
                    }
                }
            }
        }
    }
}

