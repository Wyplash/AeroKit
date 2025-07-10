package com.alex.aerokit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alex.aerokit.ui.theme.ThemeController
import com.alex.aerokit.util.Strings

/**
 * Main tools tab with navigation for each aviation tool.
 */
@Composable
fun ToolsScreen(themeController: ThemeController) {
    // Which tab is selected? (for multiple tools later)
    var selectedTab by remember { mutableStateOf(0) }

    // Tab titles (localized!) - must be inside the composable function
    val tabTitles = listOf(Strings.windComponent(themeController.language))

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { i, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == i,
                    onClick = { selectedTab = i }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Show the correct tool based on tab
        when (selectedTab) {
            0 -> WindComponentScreen(themeController) // Pass themeController if you want to localize this screen too
            // Later: 1 -> RadioRangeScreen(themeController), etc.
        }
    }
}
