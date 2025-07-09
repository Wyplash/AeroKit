package com.alex.aerokit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerokit.ui.components.WindGraphic
import com.alex.aerokit.util.parseRunwayInput
import com.alex.aerokit.util.validateRunway
import com.alex.aerokit.util.validateWindDir
import kotlin.math.*

fun validateWindSpeed(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> null
}

fun validateCrossLimit(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> null
}

@Composable
fun WindComponentScreen() {
    var runwayInput by remember { mutableStateOf("") }
    var windDirInput by remember { mutableStateOf("") }
    var windSpeedInput by remember { mutableStateOf("") }
    var crossLimitInput by remember { mutableStateOf("") }

    val runwayError = validateRunway(runwayInput)
    val windDirError = validateWindDir(windDirInput)
    val windSpeedError = validateWindSpeed(windSpeedInput)
    val crossLimitError = validateCrossLimit(crossLimitInput)

    // Parsed values
    val runwayDeg = parseRunwayInput(runwayInput)
    val windDirDeg = windDirInput.toIntOrNull() ?: 0
    val windSpeed = windSpeedInput.toIntOrNull() ?: 0
    val crossLimit = crossLimitInput.toIntOrNull() ?: 0

    // Only allow calc/arrow when all errors are null and non-blank
    val canDrawArrow = runwayError == null && windDirError == null &&
            windSpeedError == null && crossLimitError == null &&
            runwayInput.isNotBlank() && windDirInput.isNotBlank() && windSpeedInput.isNotBlank()

    val deltaRad = Math.toRadians(((windDirDeg - runwayDeg + 360) % 360).toDouble())
    val crosswind = if (canDrawArrow) windSpeed * sin(deltaRad) else 0.0
    val headwind = if (canDrawArrow) windSpeed * cos(deltaRad) else 0.0

    val crossLabel = if (!canDrawArrow) "--"
    else "${abs(crosswind.roundToInt())} kt ${if (crosswind < 0) "Left" else "Right"}"
    val headLabel = if (!canDrawArrow) "--"
    else if (headwind >= 0) "Headwind: ${abs(headwind.roundToInt())} kt"
    else "Tailwind: ${abs(headwind.roundToInt())} kt"

    val crossLimitExceeded = canDrawArrow && crossLimit > 0 && abs(crosswind) > crossLimit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        // Input fields
        OutlinedTextField(
            value = runwayInput,
            onValueChange = { runwayInput = it },
            label = { Text("Runway (09, 26, or 264°)") },
            isError = runwayError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        runwayError?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = windDirInput,
            onValueChange = { windDirInput = it },
            label = { Text("Wind Direction (°)") },
            isError = windDirError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        windDirError?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = windSpeedInput,
            onValueChange = { windSpeedInput = it },
            label = { Text("Wind Speed (kt)") },
            isError = windSpeedError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        windSpeedError?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = crossLimitInput,
            onValueChange = { crossLimitInput = it },
            label = { Text("Crosswind Limit (kt, optional)") },
            isError = crossLimitError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        crossLimitError?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))

        // Always show runway. Show arrow only if valid.
        WindGraphic(
            runwayHeading = runwayDeg,
            windDir = windDirDeg,
            windSpeed = windSpeed,
            crossLimit = crossLimit,
            showArrow = canDrawArrow
        )

        Spacer(Modifier.height(24.dp))

        if (canDrawArrow) {
            Text(
                headLabel,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "Crosswind: $crossLabel",
                color = if (crossLimitExceeded) Color.Red else Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
