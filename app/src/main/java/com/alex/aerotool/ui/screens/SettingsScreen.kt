package com.alex.aerotool.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.theme.ThemeController
import com.alex.aerotool.ui.theme.AppLanguage
import com.alex.aerotool.util.Strings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import com.alex.aerotool.ui.theme.Aircraft
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SettingsScreen(
    themeController: ThemeController,
    clearCustomAbbreviations: (() -> Unit)? = null,
    showAircraftManagerInitially: Boolean = false,
    onAircraftManagerDismissed: () -> Unit = {}
) {
    val context = LocalContext.current

    // Aircraft manager dialog state (hoist to composable scope)
    var showAircraftManager by remember { mutableStateOf(false) }
    var showAircraftDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var aircraftNameInput by remember { mutableStateOf("") }
    var crosswindLimitInput by remember { mutableStateOf("") }
    var editingAircraft by remember { mutableStateOf<Aircraft?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showAircraftManagerInitially) {
        if (showAircraftManagerInitially) showAircraftManager = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AeroTopBar(
            title = "AeroTool"
        )

        Column(Modifier.padding(24.dp)) {
            Text(Strings.settings(), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(24.dp))

            // --- Aircraft Crosswind Limit Section ---
            var expanded by remember { mutableStateOf(false) }

            Text("Default Aircraft", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        val selected = themeController.defaultAircraft?.name ?: "Select Aircraft"
                        Text(selected)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        themeController.aircraftList.forEach { aircraft ->
                            DropdownMenuItem(
                                text = { Text("${aircraft.name} (${aircraft.crosswindLimit} kt)") },
                                onClick = {
                                    themeController.setDefaultAircraft(aircraft)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showAircraftManager = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Aircraft Crosswind Limits")
            }
            Spacer(Modifier.height(24.dp))

            // Language selector
            Text(Strings.language())
            Spacer(Modifier.height(8.dp))
            LanguageSelector(
                selected = themeController.language,
                onSelected = { themeController.updateLanguage(it) }
            )
            Spacer(Modifier.height(24.dp))

            // Decimal Precision selector
            Text("Decimal Precision", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Number of decimal places shown in conversion results",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            var precisionExpanded by remember { mutableStateOf(false) }
            OutlinedButton(
                onClick = { precisionExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${themeController.decimalPrecision} decimal places")
            }
            DropdownMenu(
                expanded = precisionExpanded,
                onDismissRequest = { precisionExpanded = false }
            ) {
                (0..6).forEach { precision ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (precision) {
                                    0 -> "0 decimal places (whole numbers)"
                                    1 -> "1 decimal place"
                                    else -> "$precision decimal places"
                                }
                            )
                        },
                        onClick = {
                            themeController.updateDecimalPrecision(precision)
                            precisionExpanded = false
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            Divider()
            Spacer(Modifier.height(20.dp))

            // Add clear custom abbreviations button
            if (clearCustomAbbreviations != null) {
                Button(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Custom Abbreviations")
                }
                Spacer(Modifier.height(24.dp))
            }

            // About/Help section
            Text(Strings.aboutHelp(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            // App version
            val version = try {
                context.packageManager
                    .getPackageInfo(context.packageName, 0).versionName ?: "?"
            } catch (e: PackageManager.NameNotFoundException) {
                "?"
            }
            Text(Strings.version(version))

            Spacer(Modifier.height(12.dp))
            Text(
                Strings.contactSupport(),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* TODO: open mail */ }
            )
            Text("support@yourdomain.com", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))

            Text(
                Strings.privacyPolicy(),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* TODO: open link */ }
            )
            Text(
                Strings.termsOfService(),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* TODO: open link */ }
            )
        }
    }

    if (showAircraftManager) {
        AlertDialog(
            onDismissRequest = { showAircraftManager = false; onAircraftManagerDismissed() },
            title = { Text("Aircraft Crosswind Limits") },
            text = {
                Column {
                    if (themeController.aircraftList.isEmpty()) {
                        Text(
                            text = "No aircraft profiles found. Tap \"Add Aircraft\" below to create one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        themeController.aircraftList.forEach { aircraft ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(aircraft.name, modifier = Modifier.weight(1f))
                                Text(
                                    "${aircraft.crosswindLimit} kt",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                IconButton(onClick = {
                                    isEditMode = true
                                    editingAircraft = aircraft
                                    aircraftNameInput = aircraft.name
                                    crosswindLimitInput = aircraft.crosswindLimit.toString()
                                    showAircraftDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    themeController.deleteAircraft(aircraft)
                                    if (themeController.defaultAircraft == aircraft) {
                                        themeController.aircraftList.firstOrNull()?.let {
                                            themeController.setDefaultAircraft(it)
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        isEditMode = false
                        aircraftNameInput = ""
                        crosswindLimitInput = ""
                        showAircraftDialog = true
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add Aircraft")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showAircraftManager = false; onAircraftManagerDismissed()
                }) {
                    Text("Done")
                }
            }
        )
        if (showAircraftDialog) {
            AlertDialog(
                onDismissRequest = { showAircraftDialog = false },
                title = { Text(if (isEditMode) "Edit Aircraft" else "Add Aircraft") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = aircraftNameInput,
                            onValueChange = { aircraftNameInput = it },
                            label = { Text("Aircraft Name") }
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = crosswindLimitInput,
                            onValueChange = {
                                crosswindLimitInput = it.filter { c -> c.isDigit() }
                            },
                            label = { Text("Crosswind Limit (kt)") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val newAircraft = Aircraft(
                            aircraftNameInput.trim(),
                            crosswindLimitInput.toIntOrNull() ?: 0
                        )
                        if (isEditMode && editingAircraft != null) {
                            themeController.editAircraft(editingAircraft!!, newAircraft)
                        } else {
                            themeController.addAircraft(newAircraft)
                            if (themeController.aircraftList.size == 1) {
                                themeController.setDefaultAircraft(newAircraft)
                            }
                        }
                        showAircraftDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAircraftDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    if (showClearConfirmDialog && clearCustomAbbreviations != null) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Are you sure you want to clear all custom abbreviations? This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    clearCustomAbbreviations()
                    showClearConfirmDialog = false
                }) {
                    Text("Yes, clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- Language selector ---

@Composable
fun LanguageSelector(selected: AppLanguage, onSelected: (AppLanguage) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        LanguageRadioButton(Strings.english(), AppLanguage.ENGLISH, selected, onSelected)
        LanguageRadioButton(Strings.french(), AppLanguage.FRENCH, selected, onSelected)
    }
}

@Composable
fun LanguageRadioButton(
    label: String,
    lang: AppLanguage,
    selected: AppLanguage,
    onSelected: (AppLanguage) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = (selected == lang),
            onClick = { onSelected(lang) }
        )
        Text(label)
    }
}
