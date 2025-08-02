package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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

    // Fuel densities at 15°C (kg/L) - corrected values
    val fuelDensities = mapOf(
        "100LL" to 0.72, // 100 Low Lead (AvGas)
        "Jet A" to 0.803, // Jet A fuel (corrected from 0.775)
        "Jet A-1" to 0.803, // Jet A-1 fuel (corrected from 0.775)
        "Mogas" to 0.745 // Motor gasoline (corrected from 0.72)
    )

    fun getCurrentDensity(): Double = fuelDensities[fuelType] ?: 0.72

    // Volume conversions - fixed conversion factors
    fun updateFromUSGallons(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            imperialGallons = (value * 0.832674).round(3)
            liters = (value * 3.785412).round(2)  // Fixed: was 3.78541
            pounds = (value * 3.785412 * density * 2.20462).round(2)
            kilograms = (value * 3.785412 * density).round(2)
        } else {
            imperialGallons = ""; liters = ""; pounds = ""; kilograms = ""
        }
    }

    fun updateFromImperialGallons(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val density = getCurrentDensity()
            usGallons = (value * 1.20095).round(3)
            liters = (value * 4.546092).round(2)  // Fixed: was 4.54609
            pounds = (value * 4.546092 * density * 2.20462).round(2)
            kilograms = (value * 4.546092 * density).round(2)
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

    // Weight conversions - fixed conversion factors
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                            "• Jet A/A-1: 0.803 kg/L (6.7 lb/gal)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Mogas: 0.745 kg/L (6.2 lb/gal)",
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

        Spacer(Modifier.height(11.dp))
        Icon(
            imageVector = Icons.Default.LocalGasStation,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(38.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Fuel",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Enter fuel quantity in any unit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fuel Type Selector Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "⛽",
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                "Fuel Type",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
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
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            // Volume Section Header
            item {
                Text(
                    "Volume",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // US Gallons
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (usGallons.isEmpty() && imperialGallons.isEmpty() && liters.isEmpty() && pounds.isEmpty() && kilograms.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "usgal") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = if (lastEdited == "usgal") androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .heightIn(min = 64.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "🛢",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "US Gallons",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(2.2f)
                                .padding(end = 2.dp)
                        )
                        Box(
                            Modifier
                                .fillMaxWidth(0.46f)
                                .height(56.dp)
                        ) {
                            Row(
                                Modifier.matchParentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = usGallons,
                                    onValueChange = {
                                        usGallons = it
                                        lastEdited = "usgal"
                                    },
                                    label = null,
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        textAlign = TextAlign.End,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        updateFromUSGallons(usGallons)
                                    }),
                                    colors = if (lastEdited == "usgal") OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ) else OutlinedTextFieldDefaults.colors(),
                                    enabled = lastEdited == "usgal" || usGallons.isEmpty()
                                )
                                Text(
                                    "gal",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "usgal") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 6.dp)
                                        .widthIn(min = 32.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            // Imperial Gallons
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (usGallons.isEmpty() && imperialGallons.isEmpty() && liters.isEmpty() && pounds.isEmpty() && kilograms.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "impgal") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = if (lastEdited == "impgal") androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .heightIn(min = 64.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "🏺",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Imperial Gallons",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(2.2f)
                                .padding(end = 2.dp)
                        )
                        Box(
                            Modifier
                                .fillMaxWidth(0.46f)
                                .height(56.dp)
                        ) {
                            Row(
                                Modifier.matchParentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = imperialGallons,
                                    onValueChange = {
                                        imperialGallons = it
                                        lastEdited = "impgal"
                                    },
                                    label = null,
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        textAlign = TextAlign.End,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        updateFromImperialGallons(imperialGallons)
                                    }),
                                    colors = if (lastEdited == "impgal") OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ) else OutlinedTextFieldDefaults.colors(),
                                    enabled = lastEdited == "impgal" || imperialGallons.isEmpty()
                                )
                                Text(
                                    "gal",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "impgal") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 6.dp)
                                        .widthIn(min = 32.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            // Liters
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (usGallons.isEmpty() && imperialGallons.isEmpty() && liters.isEmpty() && pounds.isEmpty() && kilograms.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "liters") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = if (lastEdited == "liters") androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .heightIn(min = 64.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "💧",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Liters",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(2.2f)
                                .padding(end = 2.dp)
                        )
                        Box(
                            Modifier
                                .fillMaxWidth(0.46f)
                                .height(56.dp)
                        ) {
                            Row(
                                Modifier.matchParentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = liters,
                                    onValueChange = {
                                        liters = it
                                        lastEdited = "liters"
                                    },
                                    label = null,
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        textAlign = TextAlign.End,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        updateFromLiters(liters)
                                    }),
                                    colors = if (lastEdited == "liters") OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ) else OutlinedTextFieldDefaults.colors(),
                                    enabled = lastEdited == "liters" || liters.isEmpty()
                                )
                                Text(
                                    "L",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "liters") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 6.dp)
                                        .widthIn(min = 32.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }

            // Weight Section Header
            item {
                Text(
                    "Weight",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Pounds
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (usGallons.isEmpty() && imperialGallons.isEmpty() && liters.isEmpty() && pounds.isEmpty() && kilograms.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "pounds") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = if (lastEdited == "pounds") androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .heightIn(min = 64.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "⚖️",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Pounds",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(2.2f)
                                .padding(end = 2.dp)
                        )
                        Box(
                            Modifier
                                .fillMaxWidth(0.46f)
                                .height(56.dp)
                        ) {
                            Row(
                                Modifier.matchParentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = pounds,
                                    onValueChange = {
                                        pounds = it
                                        lastEdited = "pounds"
                                    },
                                    label = null,
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        textAlign = TextAlign.End,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        updateFromPounds(pounds)
                                    }),
                                    colors = if (lastEdited == "pounds") OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ) else OutlinedTextFieldDefaults.colors(),
                                    enabled = lastEdited == "pounds" || pounds.isEmpty()
                                )
                                Text(
                                    "lb",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "pounds") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 6.dp)
                                        .widthIn(min = 32.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            // Kilograms
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (usGallons.isEmpty() && imperialGallons.isEmpty() && liters.isEmpty() && pounds.isEmpty() && kilograms.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "kg") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = if (lastEdited == "kg") androidx.compose.foundation.BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.outline
                    ) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .heightIn(min = 64.dp)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "🏋️",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Kilograms",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .weight(2.2f)
                                .padding(end = 2.dp)
                        )
                        Box(
                            Modifier
                                .fillMaxWidth(0.46f)
                                .height(56.dp)
                        ) {
                            Row(
                                Modifier.matchParentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = kilograms,
                                    onValueChange = {
                                        kilograms = it
                                        lastEdited = "kg"
                                    },
                                    label = null,
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        textAlign = TextAlign.End,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = {
                                        updateFromKilograms(kilograms)
                                    }),
                                    colors = if (lastEdited == "kg") OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ) else OutlinedTextFieldDefaults.colors(),
                                    enabled = lastEdited == "kg" || kilograms.isEmpty()
                                )
                                Text(
                                    "kg",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "kg") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 6.dp)
                                        .widthIn(min = 32.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // Clear button
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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