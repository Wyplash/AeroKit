package com.alex.aerotool.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alex.aerotool.data.LanguagePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class AppLanguage { ENGLISH, FRENCH }

class ThemeController(
    initialLanguage: AppLanguage,
    private val languagePreference: LanguagePreference
) {
    var language by mutableStateOf(initialLanguage)

    // WindComponent persistent fields
    var runwayInput by mutableStateOf("")
    var windDirInput by mutableStateOf("")
    var windSpeedInput by mutableStateOf("")
    var crossLimitInput by mutableStateOf("")

    fun updateLanguage(newLanguage: AppLanguage) {
        language = newLanguage
        CoroutineScope(Dispatchers.IO).launch {
            languagePreference.setLanguage(newLanguage)
        }
    }
}
