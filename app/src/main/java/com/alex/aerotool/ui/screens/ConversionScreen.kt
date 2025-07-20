package com.alex.aerotool.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.theme.ThemeController

// Models a conversion category
private data class ConversionCategory(
    val icon: ImageVector,
    val label: String,
    val color: androidx.compose.ui.graphics.Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversionScreen(themeController: ThemeController) {
    val categories = listOf(
        ConversionCategory(
            icon = Icons.Default.Speed,
            label = "Speed",
            color = MaterialTheme.colorScheme.primary
        ),
        ConversionCategory(
            icon = Icons.Default.DeviceThermostat,
            label = "Temperature",
            color = MaterialTheme.colorScheme.secondary
        ),
        ConversionCategory(
            icon = Icons.Default.Straighten,
            label = "Length/Distance",
            color = MaterialTheme.colorScheme.tertiary
        ),
        ConversionCategory(
            icon = Icons.Default.LineWeight,
            label = "Weight",
            color = MaterialTheme.colorScheme.primary
        ),
        ConversionCategory(
            icon = Icons.Default.WaterDrop,
            label = "Volume",
            color = MaterialTheme.colorScheme.secondary
        ),
        ConversionCategory(
            icon = Icons.Default.Explore,
            label = "Geo Coordinate",
            color = MaterialTheme.colorScheme.tertiary
        ),
        ConversionCategory(
            icon = Icons.Default.AccessTime,
            label = "Time",
            color = MaterialTheme.colorScheme.primary
        ),
        ConversionCategory(
            icon = Icons.Default.TrendingUp,
            label = "Climb Gradient",
            color = MaterialTheme.colorScheme.secondary
        ),
        ConversionCategory(
            icon = Icons.Default.Compress,
            label = "Pressure",
            color = MaterialTheme.colorScheme.primary
        ),
        ConversionCategory(
            icon = Icons.Default.LocalGasStation,
            label = "Fuel",
            color = MaterialTheme.colorScheme.secondary
        )
    )

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showTempInfo by remember { mutableStateOf(false) }
    var showSpeedInfo by remember { mutableStateOf(false) }
    var showLengthInfo by remember { mutableStateOf(false) }
    var showWeightInfo by remember { mutableStateOf(false) }
    var showVolumeInfo by remember { mutableStateOf(false) }
    var showGeoInfo by remember { mutableStateOf(false) }
    var showTimeInfo by remember { mutableStateOf(false) }
    var showClimbInfo by remember { mutableStateOf(false) }
    var showPressureInfo by remember { mutableStateOf(false) }
    var showFuelInfo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (selectedCategory == null) {
            AeroTopBar(
                title = "AeroTool"
            )
        } else {
            AeroTopBar(
                title = "AeroTool",
                onBackClick = { selectedCategory = null },
                onInfoClick = {
                    when (selectedCategory) {
                        "Speed" -> showSpeedInfo = true
                        "Temperature" -> showTempInfo = true
                        "Length/Distance" -> showLengthInfo = true
                        "Weight" -> showWeightInfo = true
                        "Volume" -> showVolumeInfo = true
                        "Geo Coordinate" -> showGeoInfo = true
                        "Time" -> showTimeInfo = true
                        "Climb Gradient" -> showClimbInfo = true
                        "Pressure" -> showPressureInfo = true
                        "Fuel" -> showFuelInfo = true
                    }
                }
            )
        }

        when (selectedCategory) {
            "Speed" -> SpeedConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showSpeedInfo,
                onInfoDismiss = { showSpeedInfo = false }
            )
            "Temperature" -> TemperatureConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showTempInfo,
                onInfoDismiss = { showTempInfo = false }
            )
            "Length/Distance" -> LengthConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showLengthInfo,
                onInfoDismiss = { showLengthInfo = false }
            )

            "Weight" -> WeightConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showWeightInfo,
                onInfoDismiss = { showWeightInfo = false }
            )

            "Volume" -> VolumeConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showVolumeInfo,
                onInfoDismiss = { showVolumeInfo = false }
            )

            "Geo Coordinate" -> GeoCoordinateScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showGeoInfo,
                onInfoDismiss = { showGeoInfo = false }
            )

            "Time" -> TimeConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showTimeInfo,
                onInfoDismiss = { showTimeInfo = false }
            )
            "Climb Gradient" -> ClimbGradientConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showClimbInfo,
                onInfoDismiss = { showClimbInfo = false }
            )
            "Pressure" -> PressureConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showPressureInfo,
                onInfoDismiss = { showPressureInfo = false }
            )
            "Fuel" -> FuelConversionScreen(
                themeController = themeController,
                onBack = { selectedCategory = null },
                showInfo = showFuelInfo,
                onInfoDismiss = { showFuelInfo = false }
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { cat ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cat.color.copy(alpha = 0.09f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                selectedCategory = cat.label
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                cat.icon,
                                contentDescription = cat.label,
                                tint = cat.color,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = cat.label,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
