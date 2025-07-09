package com.alex.aerokit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Main tools tab with navigation for each aviation tool.
 */
@Composable
fun ToolsScreen() {
    // Which tab is selected? (for multiple tools later)
    var selectedTab by remember { mutableStateOf(0) }

    // Tab titles -- you can add more tools in the future!
    val tabTitles = listOf("Wind Component")

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
            0 -> WindComponentScreen()
            // Later: 1 -> RadioRangeScreen(), 2 -> SomeOtherToolScreen(), etc.
        }
    }
}
