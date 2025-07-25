package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.components.WindGraphic
import com.alex.aerotool.util.parseRunwayInput
import com.alex.aerotool.util.validateRunway
import com.alex.aerotool.util.validateWindDir
import com.alex.aerotool.util.validateWindSpeed
import com.alex.aerotool.util.validateCrossLimit
import com.alex.aerotool.ui.theme.ThemeController
import com.alex.aerotool.ui.theme.AppLanguage
import com.alex.aerotool.util.Strings
import kotlin.math.*

@Composable
fun WindComponentScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null
) {
    val lang = themeController.language
    var showInfo by remember { mutableStateOf(false) }

    // Show info dialog when requested
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("Wind Components Help") },
            text = {
                Text(
                    when (lang) {
                        com.alex.aerotool.ui.theme.AppLanguage.ENGLISH ->
                            "How to use Wind Components\n\n" +
                                    "1. Enter the runway identifier (e.g., 09, 26, 264).\n" +
                                    "2. Input the wind direction (°) and wind speed (kt).\n" +
                                    "3. For 'X Wind (kt)', type your own crosswind limit or select an aircraft from the dropdown to auto-fill this value.\n" +
                                    "4. If you leave the 'X Wind (kt)' field blank, the selected aircraft’s crosswind limit will be used.\n" +
                                    "5. The results will show headwind and crosswind components. If the crosswind exceeds your chosen limit, the value will display in red.\n\n" +
                                    "Use this tool to help decide if conditions are within safe crosswind limits for you and your aircraft."

                        com.alex.aerotool.ui.theme.AppLanguage.FRENCH ->
                            "Comment utiliser les composantes de vent\n\n" +
                                    "1. Entrez l’identifiant de piste (ex : 09, 26, 264).\n" +
                                    "2. Entrez la direction (°) et la vitesse du vent (kt).\n" +
                                    "3. Pour 'X Wind (kt)', saisissez votre limite ou sélectionnez un avion dans la liste déroulante pour auto-remplir la valeur.\n" +
                                    "4. Si vous laissez 'X Wind (kt)' vide, la limite de l’avion sélectionné sera utilisée.\n" +
                                    "5. Les résultats afficheront les composantes vent de face et vent traversier. Si la limite est dépassée, la valeur de vent traversier sera affichée en rouge.\n\n" +
                                    "Utilisez cet outil pour vérifier si les conditions correspondent à vos limites personnelles ou à celles de votre avion."
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

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
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

            // Manual + Aircraft crosswind limit section
            var selectedAircraft by remember { mutableStateOf(themeController.defaultAircraft) }
            var showAircraftDropdown by remember { mutableStateOf(false) }
            // On first load, set dropdown to default aircraft
            LaunchedEffect(Unit) {
                if (themeController.defaultAircraft != null) {
                    selectedAircraft = themeController.defaultAircraft
                }
            }

            // Use manual if present, otherwise fallback to selected aircraft limit
            val effectiveCrossLimit =
                crossLimitInput.toIntOrNull() ?: selectedAircraft?.crosswindLimit ?: 0

            val crossLimitExceeded =
                canDrawArrow && effectiveCrossLimit > 0 && abs(crosswind) > effectiveCrossLimit

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

            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                // X Wind manual input
                OutlinedTextField(
                    value = crossLimitInput,
                    onValueChange = { crossLimitInput = it },
                    label = { Text("X Wind (kt)") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        if (crossLimitInput.isNotEmpty()) {
                            IconButton(onClick = { crossLimitInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear X Wind")
                            }
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                // Aircraft Dropdown as OutlinedTextField
                OutlinedTextField(
                    value = selectedAircraft?.name ?: "Select Aircraft",
                    onValueChange = {}, // not editable manually
                    readOnly = true,
                    enabled = true,
                    label = { Text("Aircraft") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    visualTransformation = VisualTransformation.None,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                    trailingIcon = {
                        IconButton(
                            onClick = { showAircraftDropdown = true },
                            modifier = Modifier.padding(end = 2.dp)
                        ) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    },
                    // Add horizontal content padding to match label and text
                    placeholder = null
                )
                DropdownMenu(
                    expanded = showAircraftDropdown,
                    onDismissRequest = { showAircraftDropdown = false }
                ) {
                    themeController.aircraftList.forEach { aircraft ->
                        DropdownMenuItem(
                            text = { Text("${aircraft.name} (${aircraft.crosswindLimit} kt)") },
                            onClick = {
                                selectedAircraft = aircraft
                                showAircraftDropdown = false
                            }
                        )
                    }
                }
            }
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
}
