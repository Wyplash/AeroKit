package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.components.RadioGraphic
import com.alex.aerotool.ui.theme.ThemeController
import com.alex.aerotool.ui.theme.AppLanguage
import com.alex.aerotool.util.Strings
import kotlin.math.*

@Composable
fun RadioRangeScreen(themeController: ThemeController, onBack: (() -> Unit)? = null) {
    val lang = themeController.language
    var showInfo by remember { mutableStateOf(false) }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("Radio Range Tool") },
            text = {
                Text(
                    when (lang) {
                        AppLanguage.ENGLISH ->
                            "Estimates VHF/UHF line-of-sight radio range using standard aviation formulas. " +
                            "Slant distance (LOS) and geometric ground distance are visualized. " +
                            "Used for planning comm/nav coverage.\n\n" +
                            "LOS (NM) = 1.23 × (√Altitude₁(ft) + √Altitude₂(ft))"
                        AppLanguage.FRENCH ->
                            "Estime la portée radio VHF/UHF en ligne de vue en utilisant les formules aéronautiques standard. " +
                                    "La distance oblique (LOS) et la distance géométrique au sol sont visualisées. " +
                                    "Utilisé pour planifier la couverture comm/nav.\n\n" +
                                    "LOS (NM) = 1,23 × (√Altitude₁(ft) + √Altitude₂(ft))"
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

        // Input fields (only altitudes)
        var aircraftAltitude by remember { mutableStateOf("5000") }
        var radioStationAltitude by remember { mutableStateOf("100") }

        // Validation
        val aircraftAltError = validateAltitude(aircraftAltitude)
        val radioAltError = validateAltitude(radioStationAltitude)

        // Calculate line-of-sight distance based on altitudes
        val aircraftAltValue = aircraftAltitude.toDoubleOrNull() ?: 0.0
        val radioAltValue = radioStationAltitude.toDoubleOrNull() ?: 0.0
        val lineOfSightDistance = calculateLineOfSightDistance(
            aircraftAltFt = aircraftAltValue,
            radioAltFt = radioAltValue
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Altitude Section
            Text(
                text = when (lang) {
                    AppLanguage.ENGLISH -> "Altitudes"
                    AppLanguage.FRENCH -> "Altitudes"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = aircraftAltitude,
                onValueChange = { aircraftAltitude = it },
                label = { Text(Strings.aircraftAltitudeLabel(lang)) },
                isError = aircraftAltError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            aircraftAltError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = radioStationAltitude,
                onValueChange = { radioStationAltitude = it },
                label = { Text(Strings.radioStationAltitudeLabel(lang)) },
                isError = radioAltError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            radioAltError?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(24.dp))

            // Visual representation
            RadioGraphic(
                aircraftAltitude = aircraftAltValue,
                radioStationAltitude = radioAltValue,
                horizontalDistance = lineOfSightDistance,
                showGraphic = true,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Always show result card/answer
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (lang) {
                            AppLanguage.ENGLISH -> "Line-of-Sight Distance"
                            AppLanguage.FRENCH -> "Distance en ligne de vue"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = String.format(
                            "%.1f NM",
                            if (aircraftAltError == null && radioAltError == null &&
                                aircraftAltitude.isNotBlank() && radioStationAltitude.isNotBlank()
                            ) lineOfSightDistance else 0.0
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = when (lang) {
                            AppLanguage.ENGLISH -> "This is the geometric distance between the aircraft and the radio station, considering Earth's curvature. Actual achievable range may be reduced due to obstacles or weather."
                            AppLanguage.FRENCH -> "Ceci est la distance géométrique entre l'aéronef et la station radio, en tenant compte de la courbure de la Terre. La portée réelle peut être réduite par des obstacles ou la météo."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun calculateLineOfSightDistance(aircraftAltFt: Double, radioAltFt: Double): Double {
    // FAA Radio Horizon Formula:
    // LOS (NM) = 1.23 × (√(aircraft altitude ft) + √(antenna altitude ft))
    return 1.23 * (sqrt(aircraftAltFt) + sqrt(radioAltFt))
}

private fun validateAltitude(input: String): String? {
    return when {
        input.isBlank() -> null
        input.toDoubleOrNull() == null -> "Invalid altitude"
        input.toDouble() < 0 -> "Altitude cannot be negative"
        input.toDouble() > 100000 -> "Altitude too high"
        else -> null
    }
}