package com.alex.aerokit.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ThemeController {
    var themeMode by mutableStateOf(AppThemeMode.SYSTEM)
}
