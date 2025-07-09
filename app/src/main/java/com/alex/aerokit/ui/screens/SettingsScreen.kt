// file: ui/screens/SettingsScreen.kt
package com.alex.aerokit.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.alex.aerokit.ui.theme.ThemeController
import com.alex.aerokit.ui.theme.AppThemeMode
import com.alex.aerokit.ui.theme.AppLanguage


@Composable
fun SettingsScreen(themeController: ThemeController) {
    val context = LocalContext.current

    Column(Modifier.padding(24.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        // Theme selector
        Text("Theme")
        Spacer(Modifier.height(8.dp))
        ThemeModeSelector(
            selected = themeController.themeMode,
            onSelected = { themeController.themeMode = it }
        )
        Spacer(Modifier.height(20.dp))

        // Language selector
        Text("Language")
        Spacer(Modifier.height(8.dp))
        LanguageSelector(
            selected = themeController.language,
            onSelected = { themeController.language = it }
        )
        Spacer(Modifier.height(24.dp))

        Divider()
        Spacer(Modifier.height(20.dp))

        // About/Help section
        Text("About / Help", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        // App version
        val version = try {
            context.packageManager
                .getPackageInfo(context.packageName, 0).versionName ?: "?"
        } catch (e: PackageManager.NameNotFoundException) { "?" }
        Text("Version: $version")

        Spacer(Modifier.height(12.dp))
        Text(
            "Contact / Support / Feedback",
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO: open mail */ }
        )
        Text("support@yourdomain.com", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))

        Text(
            "Privacy Policy",
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO: open link */ }
        )
        Text(
            "Terms of Service",
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO: open link */ }
        )
    }
}

@Composable
fun ThemeModeSelector(selected: AppThemeMode, onSelected: (AppThemeMode) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        ThemeRadioButton("Light", AppThemeMode.LIGHT, selected, onSelected)
        ThemeRadioButton("Dark", AppThemeMode.DARK, selected, onSelected)
        ThemeRadioButton("Auto", AppThemeMode.SYSTEM, selected, onSelected)
    }
}

@Composable
fun ThemeRadioButton(
    label: String,
    mode: AppThemeMode,
    selected: AppThemeMode,
    onSelected: (AppThemeMode) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = (selected == mode),
            onClick = { onSelected(mode) }
        )
        Text(label)
    }
}

// --- Language selector ---

@Composable
fun LanguageSelector(selected: AppLanguage, onSelected: (AppLanguage) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        LanguageRadioButton("English", AppLanguage.ENGLISH, selected, onSelected)
        LanguageRadioButton("Français", AppLanguage.FRENCH, selected, onSelected)
    }
}

@Composable
fun LanguageRadioButton(
    label: String,
    lang: AppLanguage,
    selected: AppLanguage,
    onSelected: (AppLanguage) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = (selected == lang),
            onClick = { onSelected(lang) }
        )
        Text(label)
    }
}

