package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.util.Strings
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun ConversionScreen(themeController: ThemeController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AeroTopBar(
            title = "AeroTool"
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(Strings.conversionComingSoon(themeController.language))
        }
    }
}
