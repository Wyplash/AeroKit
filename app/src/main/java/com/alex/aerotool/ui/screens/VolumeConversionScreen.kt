package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun VolumeConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var liters by remember { mutableStateOf("") }
    var gallons by remember { mutableStateOf("") }
    var quarts by remember { mutableStateOf("") }
    var pints by remember { mutableStateOf("") }
    var cups by remember { mutableStateOf("") }
    var fluidOunces by remember { mutableStateOf("") }
    var milliliters by remember { mutableStateOf("") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Conversion functions
    fun updateFromLiters(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            gallons = (value * 0.264172).round(4)
            quarts = (value * 1.05669).round(4)
            pints = (value * 2.11338).round(4)
            cups = (value * 4.22675).round(4)
            fluidOunces = (value * 33.814).round(2)
            milliliters = (value * 1000).round(2)
        } else {
            gallons = ""; quarts = ""; pints = ""; cups = ""; fluidOunces = ""; milliliters = ""
        }
    }

    fun updateFromGallons(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            liters = (value / 0.264172).round(4)
            quarts = (value * 4).round(4)
            pints = (value * 8).round(4)
            cups = (value * 16).round(4)
            fluidOunces = (value * 128).round(2)
            milliliters = (value * 3785.41).round(2)
        } else {
            liters = ""; quarts = ""; pints = ""; cups = ""; fluidOunces = ""; milliliters = ""
        }
    }

    fun updateFromQuarts(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            liters = (value / 1.05669).round(4)
            gallons = (value / 4).round(4)
            pints = (value * 2).round(4)
            cups = (value * 4).round(4)
            fluidOunces = (value * 32).round(2)
            milliliters = (value * 946.353).round(2)
        } else {
            liters = ""; gallons = ""; pints = ""; cups = ""; fluidOunces = ""; milliliters = ""
        }
    }

    fun updateFromPints(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            liters = (value / 2.11338).round(4)
            gallons = (value / 8).round(4)
            quarts = (value / 2).round(4)
            cups = (value * 2).round(4)
            fluidOunces = (value * 16).round(2)
            milliliters = (value * 473.176).round(2)
        } else {
            liters = ""; gallons = ""; quarts = ""; cups = ""; fluidOunces = ""; milliliters = ""
        }
    }

    fun updateFromCups(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            liters = (value / 4.22675).round(4)
            gallons = (value / 16).round(4)
            quarts = (value / 4).round(4)
            pints = (value / 2).round(4)
            fluidOunces = (value * 8).round(2)
            milliliters = (value * 236.588).round(2)
        } else {
            liters = ""; gallons = ""; quarts = ""; pints = ""; fluidOunces = ""; milliliters = ""
        }
    }

    fun updateFromFluidOunces(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            liters = (value / 33.814).round(4)
            gallons = (value / 128).round(4)
            quarts = (value / 32).round(4)
            pints = (value / 16).round(4)
            cups = (value / 8).round(4)
            milliliters = (value * 29.5735).round(2)
        } else {
            liters = ""; gallons = ""; quarts = ""; pints = ""; cups = ""; milliliters = ""
        }
    }

    fun updateFromMilliliters(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            liters = (value / 1000).round(4)
            gallons = (value / 3785.41).round(4)
            quarts = (value / 946.353).round(4)
            pints = (value / 473.176).round(4)
            cups = (value / 236.588).round(4)
            fluidOunces = (value / 29.5735).round(2)
        } else {
            liters = ""; gallons = ""; quarts = ""; pints = ""; cups = ""; fluidOunces = ""
        }
    }

    fun clearAll() {
        liters = ""; gallons = ""; quarts = ""; pints = ""; cups = ""; fluidOunces =
            ""; milliliters = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Volume Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a value in any volume unit. The other fields update instantly.")
                        Spacer(Modifier.height(7.dp))
                        Text("Supported Units:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Liters (L) - Metric base unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("• US Gallons (gal)", style = MaterialTheme.typography.bodySmall)
                        Text("• US Quarts (qt)", style = MaterialTheme.typography.bodySmall)
                        Text("• US Pints (pt)", style = MaterialTheme.typography.bodySmall)
                        Text("• US Cups (cup)", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "• US Fluid Ounces (fl oz)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("• Milliliters (mL)", style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { onInfoDismiss?.invoke() }) { Text("OK") }
                }
            )
        }

        Spacer(Modifier.height(10.dp))
        Icon(
            Icons.Default.WaterDrop,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter a value in any volume unit",
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
                    liters.isBlank() && gallons.isBlank() && quarts.isBlank() && pints.isBlank() && cups.isBlank() && fluidOunces.isBlank() && milliliters.isBlank()

                // Liters
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "L") Color(
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
                            lastEdited = "L"
                            updateFromLiters(it)
                        },
                        label = { Text("Liters (L)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "L" || liters.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Gallons
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "gal") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = gallons,
                        onValueChange = {
                            gallons = it
                            lastEdited = "gal"
                            updateFromGallons(it)
                        },
                        label = { Text("Gallons (gal)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "gal" || gallons.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Quarts
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "qt") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = quarts,
                        onValueChange = {
                            quarts = it
                            lastEdited = "qt"
                            updateFromQuarts(it)
                        },
                        label = { Text("Quarts (qt)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "qt" || quarts.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Pints
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "pt") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = pints,
                        onValueChange = {
                            pints = it
                            lastEdited = "pt"
                            updateFromPints(it)
                        },
                        label = { Text("Pints (pt)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "pt" || pints.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Cups
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "cup") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = cups,
                        onValueChange = {
                            cups = it
                            lastEdited = "cup"
                            updateFromCups(it)
                        },
                        label = { Text("Cups (cup)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "cup" || cups.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Fluid Ounces
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "floz") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = fluidOunces,
                        onValueChange = {
                            fluidOunces = it
                            lastEdited = "floz"
                            updateFromFluidOunces(it)
                        },
                        label = { Text("Fluid Ounces (fl oz)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "floz" || fluidOunces.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Milliliters
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "mL") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = milliliters,
                        onValueChange = {
                            milliliters = it
                            lastEdited = "mL"
                            updateFromMilliliters(it)
                        },
                        label = { Text("Milliliters (mL)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "mL" || milliliters.isEmpty(),
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