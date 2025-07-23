package com.alex.aerotool.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Thermostat
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
import com.alex.aerotool.ui.components.CalculatorFabAndSheet
import com.alex.aerotool.ui.theme.AviationGold
import com.alex.aerotool.ui.theme.ThemeController

// Models a conversion category
private data class ConversionCategory(
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversionScreen(
    themeController: ThemeController,
    calculatorExpression: String,
    calculatorResult: String,
    setCalculatorState: (String, String) -> Unit
) {
    val categories = listOf(
        ConversionCategory(
            icon = Icons.Outlined.Speed,
            label = "Speed"
        ),
        ConversionCategory(
            icon = Icons.Outlined.Thermostat,
            label = "Temperature"
        ),
        ConversionCategory(
            icon = Icons.Outlined.Straighten,
            label = "Length/Distance"
        ),
        ConversionCategory(
            icon = Icons.Outlined.FitnessCenter,
            label = "Weight"
        ),
        ConversionCategory(
            icon = Icons.Outlined.Opacity,
            label = "Volume"
        ),
        ConversionCategory(
            icon = Icons.Outlined.Explore,
            label = "Geo Coordinate"
        ),
        ConversionCategory(
            icon = Icons.Outlined.Schedule,
            label = "Time"
        ),
        ConversionCategory(
            icon = Icons.Outlined.ShowChart,
            label = "Climb Gradient"
        ),
        ConversionCategory(
            icon = Icons.Outlined.Compress,
            label = "Pressure"
        ),
        ConversionCategory(
            icon = Icons.Outlined.LocalGasStation,
            label = "Fuel"
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

    Box(Modifier.fillMaxSize()) {
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
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
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
                                    tint = AviationGold,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = cat.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        CalculatorFabAndSheet(
            expression = calculatorExpression,
            result = calculatorResult,
            setCalculatorState = setCalculatorState,
            onTimeConversionClick = { selectedCategory = "Time" },
            themeController = themeController
        )
    }
}
