package com.alex.aerotool.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.theme.ThemeController
import com.alex.aerotool.ui.theme.AppLanguage
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.tan
import kotlin.math.atan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstantDescentScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null
) {
    val lang = themeController.language
    var showInfo by remember { mutableStateOf(false) }

    // Input fields - separate for each method
    var altitudeToLoseInput by remember { mutableStateOf("") }
    var rateOfDescentInput by remember { mutableStateOf("") }
    var rateSpeedInput by remember { mutableStateOf("") }
    var rateDistanceInput by remember { mutableStateOf("") }
    var glideSlopeInput by remember { mutableStateOf("") }
    var slopeSpeedInput by remember { mutableStateOf("") }
    var slopeDistanceInput by remember { mutableStateOf("") }

    // Track which field was last modified to avoid infinite loops
    var lastModifiedField by remember { mutableStateOf<String?>(null) }
    var showResults by remember { mutableStateOf(false) }

    // Validation and parsing - separate for each method
    val altitudeToLose = altitudeToLoseInput.toDoubleOrNull()
    val inputRate = rateOfDescentInput.toDoubleOrNull()
    val rateSpeed = rateSpeedInput.toDoubleOrNull()
    val rateDistance = rateDistanceInput.toDoubleOrNull()
    val inputSlope = glideSlopeInput.toDoubleOrNull()
    val slopeSpeed = slopeSpeedInput.toDoubleOrNull()
    val slopeDistance = slopeDistanceInput.toDoubleOrNull()

    // Determine which method is being used
    val usingRateMethod = listOfNotNull(inputRate, rateSpeed, rateDistance).isNotEmpty()
    val usingSlopeMethod = listOfNotNull(inputSlope, slopeSpeed, slopeDistance).isNotEmpty()

    // Count filled fields for each method
    val rateMethodFields = listOfNotNull(inputRate, rateSpeed, rateDistance).size
    val slopeMethodFields = listOfNotNull(inputSlope, slopeSpeed, slopeDistance).size

    val canCalculate =
        altitudeToLose != null && ((usingRateMethod && rateMethodFields >= 2) || (usingSlopeMethod && slopeMethodFields >= 2))

    // Update instruction based on filled fields
    val instructionText = when (lang) {
        AppLanguage.ENGLISH -> "Enter altitude and choose what to calculate:"
        AppLanguage.FRENCH -> "Entrez altitude et choisissez quoi calculer :"
    }

    // Animation states
    val resultsAlpha by animateFloatAsState(
        targetValue = if (showResults) 1f else 0f,
        label = "resultsAlpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardColor by animateColorAsState(
        targetValue = if (showResults) primaryColor.copy(alpha = 0.1f) else surfaceColor,
        label = "cardColor"
    )

    // State for display-only result
    var resultLabel by remember { mutableStateOf("") }
    var resultValue by remember { mutableStateOf("") }
    var resultUnit by remember { mutableStateOf("") }

    // Calculate missing field manually when button is pressed
    fun calculateMissingField() {
        if (canCalculate) {
            var didCalculate = false
            when {
                usingRateMethod && inputRate != null && rateSpeed != null && rateDistanceInput.isBlank() -> {
                    val altitude = altitudeToLose ?: 0.0
                    val timeToDescend = altitude / inputRate
                    val distanceRequired = (rateSpeed * timeToDescend) / 60.0
                    resultLabel = when (lang) {
                        AppLanguage.ENGLISH -> "Required distance"
                        AppLanguage.FRENCH -> "Distance requise"
                    }
                    resultValue = if (distanceRequired.isFinite()) String.format(
                        "%.1f",
                        distanceRequired
                    ) else "--"
                    resultUnit = "NM"
                    lastModifiedField = "distance"
                    didCalculate = true
                }
                usingRateMethod && rateSpeed != null && rateDistance != null && rateOfDescentInput.isBlank() && rateDistance != 0.0 -> {
                    val altitude = altitudeToLose ?: 0.0
                    val hours = rateDistance / rateSpeed
                    val totalMinutes = hours * 60.0
                    val calculatedRate = if (totalMinutes != 0.0) altitude / totalMinutes else 0.0
                    resultLabel = when (lang) {
                        AppLanguage.ENGLISH -> "Required rate"
                        AppLanguage.FRENCH -> "Taux requis"
                    }
                    resultValue = if (calculatedRate.isFinite()) calculatedRate.roundToInt()
                        .toString() else "--"
                    resultUnit = "ft/min"
                    lastModifiedField = "rate"
                    didCalculate = true
                }
                usingRateMethod && inputRate != null && rateDistance != null && rateSpeedInput.isBlank() -> {
                    val altitude = altitudeToLose ?: 0.0
                    val timeToDescend = altitude / inputRate
                    val speedRequired =
                        if (timeToDescend != 0.0) (rateDistance * 60.0) / timeToDescend else 0.0
                    resultLabel = when (lang) {
                        AppLanguage.ENGLISH -> "Required speed"
                        AppLanguage.FRENCH -> "Vitesse requise"
                    }
                    resultValue = if (speedRequired.isFinite()) speedRequired.roundToInt()
                        .toString() else "--"
                    resultUnit = "kt"
                    lastModifiedField = "speed"
                    didCalculate = true
                }
                usingSlopeMethod && inputSlope != null && slopeSpeed != null && slopeDistanceInput.isBlank() -> {
                    val altitude = altitudeToLose ?: 0.0
                    val slopeRad = Math.toRadians(inputSlope)
                    val verticalFeetPerNM = Math.tan(slopeRad) * 6076.0
                    val distanceRequired =
                        if (verticalFeetPerNM > 0.0) altitude / verticalFeetPerNM else 0.0
                    resultLabel = when (lang) {
                        AppLanguage.ENGLISH -> "Required distance"
                        AppLanguage.FRENCH -> "Distance requise"
                    }
                    resultValue = if (distanceRequired.isFinite()) String.format(
                        "%.1f",
                        distanceRequired
                    ) else "--"
                    resultUnit = "NM"
                    lastModifiedField = "distance"
                    didCalculate = true
                }
                usingSlopeMethod && slopeSpeed != null && slopeDistance != null && glideSlopeInput.isBlank() && slopeDistance != 0.0 -> {
                    val altitude = altitudeToLose ?: 0.0
                    val deg = Math.toDegrees(Math.atan((altitude / slopeDistance) / 6076.0))
                    resultLabel = when (lang) {
                        AppLanguage.ENGLISH -> "Required slope"
                        AppLanguage.FRENCH -> "Pente requise"
                    }
                    resultValue = if (deg.isFinite()) String.format("%.1f", deg) else "--"
                    resultUnit = "°"
                    lastModifiedField = "slope"
                    didCalculate = true
                }
                usingSlopeMethod && inputSlope != null && slopeDistance != null && slopeSpeedInput.isBlank() -> {
                    val tangent = Math.tan(Math.toRadians(inputSlope))
                    val speed = tangent * 101.27 * 60.0
                    resultLabel = when (lang) {
                        AppLanguage.ENGLISH -> "Required speed"
                        AppLanguage.FRENCH -> "Vitesse requise"
                    }
                    resultValue = if (speed.isFinite()) speed.roundToInt().toString() else "--"
                    resultUnit = "kt"
                    lastModifiedField = "speed"
                    didCalculate = true
                }
            }
            showResults = didCalculate
        } else {
            showResults = false
        }
    }
    // For result panel visibility
    val showResultPanel = showResults && canCalculate

    // Show info dialog when requested
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Constant Descent Calculator")
                }
            },
            text = {
                Text(
                    when (lang) {
                        AppLanguage.ENGLISH ->
                            "Calculate descent parameters using any combination of fields.\n\n" +
                                    "📐 Enter any 3 of these 5 fields:\n" +
                                    "• Altitude to Lose (ft)\n" +
                                    "• Rate of Descent (ft/min)\n" +
                                    "• Glide Slope (degrees)\n" +
                                    "• Ground Speed (kt)\n" +
                                    "• Distance (NM)\n\n" +
                                    "✨ Press Calculate to find the missing values!"

                        AppLanguage.FRENCH ->
                            "Calculer les paramètres de descente avec n'importe quelle combinaison de champs.\n\n" +
                                    "📐 Entrez 3 de ces 5 champs :\n" +
                                    "• Altitude à perdre (ft)\n" +
                                    "• Taux de descente (ft/min)\n" +
                                    "• Pente (degrés)\n" +
                                    "• Vitesse sol (kt)\n" +
                                    "• Distance (NM)\n\n" +
                                    "✨ Appuyez sur Calculer pour trouver les valeurs manquantes !"
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar with back button and info button
        AeroTopBar(
            title = "AeroTool",
            onBackClick = onBack,
            onInfoClick = { showInfo = true }
        )

        // Content with scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = instructionText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Shared altitude field, compacted
            CompactInputField(
                value = altitudeToLoseInput,
                onValueChange = {
                    altitudeToLoseInput = it
                    showResults = false
                },
                label = when (lang) {
                    AppLanguage.ENGLISH -> "Altitude to lose (ft)"
                    AppLanguage.FRENCH -> "Altitude à perdre (ft)"
                },
                icon = Icons.Default.FlightTakeoff,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Two calculation columns, tight spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rate-based calculations
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = when (lang) {
                                    AppLanguage.ENGLISH -> "Classic Method"
                                    AppLanguage.FRENCH -> "Méthode classique"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = when (lang) {
                                    AppLanguage.ENGLISH -> "Fill any 2 of 3 fields"
                                    AppLanguage.FRENCH -> "Remplissez 2 des 3 champs"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp)
                            )
                        }
                        CompactInputField(
                            value = rateOfDescentInput,
                            onValueChange = {
                                rateOfDescentInput = it
                                glideSlopeInput = ""
                                slopeSpeedInput = ""
                                slopeDistanceInput = ""
                                showResults = false
                            },
                            label = when (lang) {
                                AppLanguage.ENGLISH -> "Rate (ft/min)"
                                AppLanguage.FRENCH -> "Taux (ft/min)"
                            },
                            icon = Icons.Default.TrendingDown,
                            modifier = Modifier.height(44.dp)
                        )
                        CompactInputField(
                            value = rateSpeedInput,
                            onValueChange = {
                                rateSpeedInput = it
                                glideSlopeInput = ""
                                slopeSpeedInput = ""
                                slopeDistanceInput = ""
                                showResults = false
                            },
                            label = when (lang) {
                                AppLanguage.ENGLISH -> "Speed (kt)"
                                AppLanguage.FRENCH -> "Vitesse (kt)"
                            },
                            icon = Icons.Default.Speed,
                            modifier = Modifier.height(44.dp)
                        )
                        CompactInputField(
                            value = rateDistanceInput,
                            onValueChange = {
                                rateDistanceInput = it
                                glideSlopeInput = ""
                                slopeSpeedInput = ""
                                slopeDistanceInput = ""
                                showResults = false
                            },
                            label = when (lang) {
                                AppLanguage.ENGLISH -> "Distance (NM)"
                                AppLanguage.FRENCH -> "Distance (NM)"
                            },
                            icon = Icons.Default.Straighten,
                            modifier = Modifier.height(44.dp)
                        )
                    }
                }
                // Slope-based calculations
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = when (lang) {
                                    AppLanguage.ENGLISH -> "Slope Method"
                                    AppLanguage.FRENCH -> "Méthode pente"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = when (lang) {
                                    AppLanguage.ENGLISH -> "Fill any 2 of 3 fields"
                                    AppLanguage.FRENCH -> "Remplissez 2 des 3 champs"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp)
                            )
                        }
                        CompactInputField(
                            value = glideSlopeInput,
                            onValueChange = {
                                glideSlopeInput = it
                                rateOfDescentInput = ""
                                rateSpeedInput = ""
                                rateDistanceInput = ""
                                showResults = false
                            },
                            label = when (lang) {
                                AppLanguage.ENGLISH -> "Slope (°)"
                                AppLanguage.FRENCH -> "Pente (°)"
                            },
                            icon = Icons.Default.TrendingDown,
                            modifier = Modifier.height(44.dp)
                        )
                        CompactInputField(
                            value = slopeSpeedInput,
                            onValueChange = {
                                slopeSpeedInput = it
                                rateOfDescentInput = ""
                                rateSpeedInput = ""
                                rateDistanceInput = ""
                                showResults = false
                            },
                            label = when (lang) {
                                AppLanguage.ENGLISH -> "Speed (kt)"
                                AppLanguage.FRENCH -> "Vitesse (kt)"
                            },
                            icon = Icons.Default.Speed,
                            modifier = Modifier.height(44.dp)
                        )
                        CompactInputField(
                            value = slopeDistanceInput,
                            onValueChange = {
                                slopeDistanceInput = it
                                rateOfDescentInput = ""
                                rateSpeedInput = ""
                                rateDistanceInput = ""
                                showResults = false
                            },
                            label = when (lang) {
                                AppLanguage.ENGLISH -> "Distance (NM)"
                                AppLanguage.FRENCH -> "Distance (NM)"
                            },
                            icon = Icons.Default.Straighten,
                            modifier = Modifier.height(44.dp)
                        )
                        OutlinedButton(
                            onClick = {
                                glideSlopeInput = "3"
                                rateOfDescentInput = ""
                                rateSpeedInput = ""
                                rateDistanceInput = ""
                                showResults = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            contentPadding = PaddingValues(vertical = 2.dp, horizontal = 0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("3° standard", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compact action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { calculateMissingField() },
                    enabled = canCalculate,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(vertical = 7.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        when (lang) {
                            AppLanguage.ENGLISH -> "Calculate"
                            AppLanguage.FRENCH -> "Calculer"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                FilledTonalButton(
                    onClick = {
                        altitudeToLoseInput = ""
                        rateOfDescentInput = ""
                        rateSpeedInput = ""
                        rateDistanceInput = ""
                        glideSlopeInput = ""
                        slopeSpeedInput = ""
                        slopeDistanceInput = ""
                        lastModifiedField = null
                        showResults = false
                    },
                    modifier = Modifier
                        .weight(0.7f)
                        .height(44.dp),
                    contentPadding = PaddingValues(vertical = 7.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Clear", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Tight result panel
            if (showResultPanel) {
                CompactResultCard(
                    title = resultLabel,
                    value = resultValue,
                    unit = resultUnit,
                    highlight = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp, horizontal = 0.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        singleLine = true,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun CompactResultCard(
    title: String,
    value: String,
    unit: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}