package com.alex.aerokit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerokit.ui.components.WindGraphic
import kotlin.math.*

/**
 * Main Wind Component tool screen.
 * Business logic and UI together for now.
 */
@Composable
fun WindComponentScreen() {
    // 1. State
    var runwayInput by remember { mutableStateOf("") }
    var windDirInput by remember { mutableStateOf("") }
    var windSpeedInput by remember { mutableStateOf("") }
    var crossLimitInput by remember { mutableStateOf("") }

    // 2. Derived/calculated state
    val runwayHeading = parseRunway(runwayInput)
    val windDir = windDirInput.toIntOrNull() ?: 0
    val windSpeed = windSpeedInput.toIntOrNull() ?: 0
    val crossLimit = crossLimitInput.toFloatOrNull() ?: 0f

    // Wind math
    val delta = ((windDir - runwayHeading + 360) % 360).toFloat()
    val angleRad = Math.toRadians(delta.toDouble())
    val crosswind = windSpeed * sin(angleRad)
    val headwind = windSpeed * cos(angleRad)

    // For text color
    val absCross = abs(crosswind)
    val isLimitExceeded = crossLimit > 0 && absCross > crossLimit

    // Direction string
    val crossStr = if (crosswind > 0.1) "RIGHT" else if (crosswind < -0.1) "LEFT" else ""

    // 3. UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Wind Component", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // Wind graphic
        WindGraphic(
            windDir = windDir,
            windSpeed = windSpeed,
            runwayHeading = runwayHeading,
            size = 180.dp
        )
        Spacer(Modifier.height(16.dp))

        // Input fields
        OutlinedTextField(
            value = runwayInput,
            onValueChange = { runwayInput = it },
            label = { Text("Runway (09, 26, or 264°)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = windDirInput,
            onValueChange = { windDirInput = it },
            label = { Text("Wind Direction (°)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = windSpeedInput,
            onValueChange = { windSpeedInput = it },
            label = { Text("Wind Speed (kt)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = crossLimitInput,
            onValueChange = { crossLimitInput = it },
            label = { Text("Crosswind Limit (kt, optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        // Crosswind/Headwind results
        Text(
            text = if (windSpeed > 0)
                "Crosswind: ${absCross.roundToInt()} kt $crossStr"
            else
                "Crosswind: --",
            color = if (isLimitExceeded) Color.Red else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = if (windSpeed > 0)
                "Headwind: ${headwind.roundToInt()} kt"
            else
                "Headwind: --",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

// -- Helper to parse "09" → 90, "26" → 260, "264" → 264
fun parseRunway(runway: String): Int {
    val n = runway.toIntOrNull() ?: 0
    return if (runway.length <= 2) (n.coerceIn(1,36) * 10) % 360
    else ((n % 360) + 360) % 360
}
