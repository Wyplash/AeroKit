package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun FuelConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var usGallons by remember { mutableStateOf("") }
    var imperialGallons by remember { mutableStateOf("") }
    var liters by remember { mutableStateOf("") }
    var pounds by remember { mutableStateOf("") }
    var kilograms by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("100LL") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Fuel densities at 15°C (kg/L)
    val fuelDensities = mapOf(
        "100LL" to 0.72, // 100 Low Lead (AvGas)
        "Jet A" to 0.775, // Jet A fuel
        "Jet A-1" to 0.775, // Jet A-1 fuel
        "Mogas" to 0.72 // Motor gasoline
    )

    fun getCurrentDensity(): Double = fuelDensities[fuelType] ?: 0.72

    // Volume conversions
    fun updateFromUSGallons(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            imperialGallons = (value * 0.832674).round(3)
            liters = (value * 3.78541).round(2)
            pounds = (value * 3.78541 * density * 2.20462).round(2)
            kilograms = (value * 3.78541 * density).round(2)
        } else {
            imperialGallons = ""; liters = ""; pounds = ""; kilograms = ""
        }
    }

    fun updateFromImperialGallons(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            usGallons = (value * 1.20095).round(3)
            liters = (value * 4.54609).round(2)
            pounds = (value * 4.54609 * density * 2.20462).round(2)
            kilograms = (value * 4.54609 * density).round(2)
        } else {
            usGallons = ""; liters = ""; pounds = ""; kilograms = ""
        }
    }

    fun updateFromLiters(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            usGallons = (value * 0.264172).round(3)
            imperialGallons = (value * 0.219969).round(3)
            pounds = (value * density * 2.20462).round(2)
            kilograms = (value * density).round(2)
        } else {
            usGallons = ""; imperialGallons = ""; pounds = ""; kilograms = ""
        }
    }

    // Weight conversions
    fun updateFromPounds(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            kilograms = (value * 0.453592).round(2)
            liters = (value / (density * 2.20462)).round(2)
            usGallons = (liters.toDoubleOrNull()?.let { it * 0.264172 } ?: 0.0).round(3)
            imperialGallons = (liters.toDoubleOrNull()?.let { it * 0.219969 } ?: 0.0).round(3)
        } else {
            kilograms = ""; liters = ""; usGallons = ""; imperialGallons = ""
        }
    }

    fun updateFromKilograms(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            pounds = (value * 2.20462).round(2)
            liters = (value / density).round(2)
            usGallons = (liters.toDoubleOrNull()?.let { it * 0.264172 } ?: 0.0).round(3)
            imperialGallons = (liters.toDoubleOrNull()?.let { it * 0.219969 } ?: 0.0).round(3)
        } else {
            pounds = ""; liters = ""; usGallons = ""; imperialGallons = ""
        }
    }

    fun updateForFuelTypeChange() {
        when (lastEdited) {
            "usgal" -> updateFromUSGallons(usGallons)
            "impgal" -> updateFromImperialGallons(imperialGallons)
            "liters" -> updateFromLiters(liters)
            "pounds" -> updateFromPounds(pounds)
            "kg" -> updateFromKilograms(kilograms)
        }
    }

    fun clearAll() {
        usGallons = ""; imperialGallons = ""; liters = ""; pounds = ""; kilograms = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Fuel Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Convert between fuel volume and weight units using actual fuel densities.")
                        Spacer(Modifier.height(7.dp))
                        Text(
                            "Fuel Densities (at 15°C):",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            "• 100LL: 0.72 kg/L (6.0 lb/gal)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Jet A/A-1: 0.775 kg/L (6.5 lb/gal)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Mogas: 0.72 kg/L (6.0 lb/gal)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Note: Density varies with temperature. These are standard values.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { onInfoDismiss?.invoke() }) { Text("OK") }
                }
            )
        }

        Spacer(Modifier.height(10.dp))
        Icon(
            Icons.Default.LocalGasStation,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter fuel quantity in any unit",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                val isBlank =
                    usGallons.isBlank() && imperialGallons.isBlank() && liters.isBlank() && pounds.isBlank() && kilograms.isBlank()

                // Fuel Type Selector
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                        Text(
                            "Fuel Type",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            fuelDensities.keys.forEach { type ->
                                FilterChip(
                                    selected = fuelType == type,
                                    onClick = {
                                        fuelType = type
                                        updateForFuelTypeChange()
                                    },
                                    label = {
                                        Text(
                                            type,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Text("Volume", style = MaterialTheme.typography.labelMedium)

                // US Gallons
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "usgal") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = usGallons,
                        onValueChange = {
                            usGallons = it
                            lastEdited = "usgal"
                            updateFromUSGallons(it)
                        },
                        label = { Text("US Gallons") },
                        placeholder = { Text("50") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "usgal" || usGallons.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Imperial Gallons
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "impgal") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = imperialGallons,
                        onValueChange = {
                            imperialGallons = it
                            lastEdited = "impgal"
                            updateFromImperialGallons(it)
                        },
                        label = { Text("Imperial Gallons") },
                        placeholder = { Text("41.6") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "impgal" || imperialGallons.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Liters
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "liters") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = liters,
                        onValueChange = {
                            liters = it
                            lastEdited = "liters"
                            updateFromLiters(it)
                        },
                        label = { Text("Liters") },
                        placeholder = { Text("189") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "liters" || liters.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text("Weight", style = MaterialTheme.typography.labelMedium)

                // Pounds
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "pounds") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = pounds,
                        onValueChange = {
                            pounds = it
                            lastEdited = "pounds"
                            updateFromPounds(it)
                        },
                        label = { Text("Pounds") },
                        placeholder = { Text("300") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "pounds" || pounds.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Kilograms
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "kg") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = kilograms,
                        onValueChange = {
                            kilograms = it
                            lastEdited = "kg"
                            updateFromKilograms(it)
                        },
                        label = { Text("Kilograms") },
                        placeholder = { Text("136") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "kg" || kilograms.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                Spacer(Modifier.height(5.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    FilledTonalButton(
                        onClick = { clearAll(); lastEdited = null },
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("Clear", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

private fun Double.round(n: Int): String = "%.${n}f".format(this)