package com.alex.aerokit.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage { ENGLISH, FRENCH }

class ThemeController {
    var themeMode by mutableStateOf(AppThemeMode.SYSTEM)
    var language by mutableStateOf(AppLanguage.ENGLISH) // <--- Add this line!
}
