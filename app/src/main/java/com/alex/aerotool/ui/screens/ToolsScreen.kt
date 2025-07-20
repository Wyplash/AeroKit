package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.theme.ThemeController
import com.alex.aerotool.util.Strings

data class ToolItem(
    val id: Int,
    val icon: ImageVector,
    val name: String,
    val description: String
)

/**
 * Main tools tab with icon-based navigation for each aviation tool.
 */
@Composable
fun ToolsScreen(
    themeController: ThemeController,
    customAbbreviations: List<AbbreviationItem>,
    setCustomAbbreviations: (List<AbbreviationItem>) -> Unit
) {
    var selectedTool by remember { mutableStateOf<Int?>(null) }
    val lang = themeController.language

    // Available tools with their icons and descriptions
    val tools = remember(lang) {
        listOf(
            ToolItem(
                id = 0,
                icon = Icons.Default.Air,
                name = Strings.windComponent(lang),
                description = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Calculate wind components"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Calculer les composantes du vent"
                }
            ),
            ToolItem(
                id = 1,
                icon = Icons.Default.Radar,
                name = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Radio Range"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Portée Radio"
                },
                description = ""
            ),
            ToolItem(
                id = 2,
                icon = Icons.Default.RecordVoiceOver,
                name = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Phonetic Alphabet"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Alphabet Phonétique"
                },
                description = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "NATO phonetic alphabet & morse code"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Alphabet phonétique OTAN & code morse"
                }
            ),
            ToolItem(
                id = 3,
                icon = Icons.Default.Book,
                name = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Abbreviations"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Abréviations"
                },
                description = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Aviation abbreviations reference"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Référence des abréviations aéronautiques"
                }
            ),
            ToolItem(
                id = 4,
                icon = Icons.Default.TrendingDown,
                name = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Constant Descent"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Descente Constante"
                },
                description = when (lang) {
                    com.alex.aerotool.ui.theme.AppLanguage.ENGLISH -> "Calculate descent distance"
                    com.alex.aerotool.ui.theme.AppLanguage.FRENCH -> "Calculer la distance de descente"
                }
            )
            // Add more tools here as they're developed
        )
    }

    if (selectedTool == null) {
        // Show tool selection grid
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AeroTopBar(
                title = "AeroTool"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tools) { tool ->
                        ToolCard(
                            tool = tool,
                            onClick = { selectedTool = tool.id }
                        )
                    }
                }
            }
        }
    } else {
        // Show selected tool with back navigation
        when (selectedTool) {
            0 -> WindComponentScreen(
                themeController = themeController,
                onBack = { selectedTool = null }
            )

            1 -> RadioRangeScreen(
                themeController = themeController,
                onBack = { selectedTool = null }
            )
            2 -> PhoneticAlphabetScreen(
                themeController = themeController,
                onBack = { selectedTool = null }
            )
            3 -> AviationAbbreviationsScreen(
                themeController = themeController,
                onBack = { selectedTool = null },
                customAbbreviations = customAbbreviations,
                setCustomAbbreviations = setCustomAbbreviations
            )
            4 -> ConstantDescentScreen(
                themeController = themeController,
                onBack = { selectedTool = null }
            )
            // Add more tool screens here as they're developed
        }
    }
}

@Composable
private fun ToolCard(
    tool: ToolItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.name,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tool.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tool.description,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
