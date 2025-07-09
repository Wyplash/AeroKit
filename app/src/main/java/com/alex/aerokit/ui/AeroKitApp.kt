package com.alex.aerokit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CompareArrows // or SwapHoriz, depending on what you pick


@Composable
fun AeroKitApp() {
    // Bottom navigation state (0=Tools, 1=Conversion, 2=Settings)
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
                    icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Convert") }, // Change here!
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
            // Placeholder for each tab
            when (bottomTab) {
                0 -> Text("Tools Screen Coming Soon", style = MaterialTheme.typography.headlineMedium)
                1 -> Text("Conversion Screen Coming Soon", style = MaterialTheme.typography.headlineMedium)
                2 -> Text("Settings Screen Coming Soon", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
