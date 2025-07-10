package com.alex.aerokit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alex.aerokit.util.Strings // <-- Add this import!
import com.alex.aerokit.ui.theme.ThemeController

@Composable
fun ConversionScreen(themeController: ThemeController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(Strings.conversionComingSoon(themeController.language))
    }
}
