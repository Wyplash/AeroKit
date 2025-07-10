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
import com.alex.aerokit.util.validateWindSpeed
import com.alex.aerokit.util.validateCrossLimit
import com.alex.aerokit.ui.theme.ThemeController
import com.alex.aerokit.ui.theme.AppLanguage
import com.alex.aerokit.util.Strings
import kotlin.math.*

@Composable
fun WindComponentScreen(themeController: ThemeController) {
    val lang = themeController.language

    // Persistent fields: use ThemeController state directly!
    var runwayInput by themeController::runwayInput
    var windDirInput by themeController::windDirInput
    var windSpeedInput by themeController::windSpeedInput
    var crossLimitInput by themeController::crossLimitInput

    val runwayError = validateRunway(runwayInput)
    val windDirError = validateWindDir(windDirInput)
    val windSpeedError = validateWindSpeed(windSpeedInput)
    val crossLimitError = validateCrossLimit(crossLimitInput)

    // Parsed values
    val runwayDeg = parseRunwayInput(runwayInput)
    val windDirDeg = windDirInput.toIntOrNull() ?: 0
    val windSpeed = windSpeedInput.toIntOrNull() ?: 0
    val crossLimit = crossLimitInput.toIntOrNull() ?: 0

    val canDrawArrow = runwayError == null && windDirError == null &&
            windSpeedError == null && crossLimitError == null &&
            runwayInput.isNotBlank() && windDirInput.isNotBlank() && windSpeedInput.isNotBlank()

    val deltaRad = Math.toRadians(((windDirDeg - runwayDeg + 360) % 360).toDouble())
    val crosswind = if (canDrawArrow) windSpeed * sin(deltaRad) else 0.0
    val headwind = if (canDrawArrow) windSpeed * cos(deltaRad) else 0.0

    val crossLabel = if (!canDrawArrow) "--"
    else "${abs(crosswind.roundToInt())} kt " +
            if (crosswind < 0)
                if (lang == AppLanguage.FRENCH) "Gauche" else "Left"
            else
                if (lang == AppLanguage.FRENCH) "Droite" else "Right"

    val headLabel = if (!canDrawArrow) "--"
    else if (headwind >= 0)
        Strings.headwind(abs(headwind.roundToInt()), lang)
    else
        Strings.tailwind(abs(headwind.roundToInt()), lang)

    val crossLimitExceeded = canDrawArrow && crossLimit > 0 && abs(crosswind) > crossLimit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = runwayInput,
            onValueChange = { runwayInput = it },
            label = { Text(Strings.runwayLabel(lang)) },
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
            label = { Text(Strings.windDirectionLabel(lang)) },
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
            label = { Text(Strings.windSpeedLabel(lang)) },
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
            label = { Text(Strings.crosswindLimitLabel(lang)) },
            isError = crossLimitError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        crossLimitError?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))

        WindGraphic(
            runwayHeading = runwayDeg,
            windDir = windDirDeg,
            windSpeed = windSpeed,
            crossLimitExceeded = crossLimitExceeded,
            showArrow = canDrawArrow
        )

        Spacer(Modifier.height(24.dp))

        if (canDrawArrow) {
            Text(
                headLabel,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                Strings.crosswindResult(crossLabel, lang),
                color = if (crossLimitExceeded) Color.Red else MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
