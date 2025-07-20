package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun LengthConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var meters by remember { mutableStateOf("") }
    var feet by remember { mutableStateOf("") }
    var inches by remember { mutableStateOf("") }
    var kilometers by remember { mutableStateOf("") }
    var miles by remember { mutableStateOf("") }
    var nauticalMiles by remember { mutableStateOf("") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Conversion functions
    fun updateFromMeters(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            feet = (value * 3.28084).round(4)
            inches = (value * 39.3701).round(2)
            kilometers = (value / 1000).round(6)
            miles = (value * 0.000621371).round(6)
            nauticalMiles = (value * 0.000539957).round(6)
        } else {
            feet = ""; inches = ""; kilometers = ""; miles = ""; nauticalMiles = ""
        }
    }

    fun updateFromFeet(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            meters = (value / 3.28084).round(4)
            inches = (value * 12).round(2)
            kilometers = (value / 3280.84).round(6)
            miles = (value / 5280).round(6)
            nauticalMiles = (value / 6076.12).round(6)
        } else {
            meters = ""; inches = ""; kilometers = ""; miles = ""; nauticalMiles = ""
        }
    }

    fun updateFromInches(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            meters = (value / 39.3701).round(4)
            feet = (value / 12).round(4)
            kilometers = (value / 39370.1).round(6)
            miles = (value / 63360).round(6)
            nauticalMiles = (value / 72913.4).round(6)
        } else {
            meters = ""; feet = ""; kilometers = ""; miles = ""; nauticalMiles = ""
        }
    }

    fun updateFromKilometers(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            meters = (value * 1000).round(2)
            feet = (value * 3280.84).round(2)
            inches = (value * 39370.1).round(2)
            miles = (value * 0.621371).round(4)
            nauticalMiles = (value * 0.539957).round(4)
        } else {
            meters = ""; feet = ""; inches = ""; miles = ""; nauticalMiles = ""
        }
    }

    fun updateFromMiles(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            meters = (value * 1609.34).round(2)
            feet = (value * 5280).round(2)
            inches = (value * 63360).round(2)
            kilometers = (value * 1.60934).round(4)
            nauticalMiles = (value * 0.868976).round(4)
        } else {
            meters = ""; feet = ""; inches = ""; kilometers = ""; nauticalMiles = ""
        }
    }

    fun updateFromNauticalMiles(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            meters = (value * 1852).round(2)
            feet = (value * 6076.12).round(2)
            inches = (value * 72913.4).round(2)
            kilometers = (value * 1.852).round(4)
            miles = (value * 1.15078).round(4)
        } else {
            meters = ""; feet = ""; inches = ""; kilometers = ""; miles = ""
        }
    }

    fun clearAll() {
        meters = ""; feet = ""; inches = ""; kilometers = ""; miles = ""; nauticalMiles = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Length Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a value in any length unit. The other fields update instantly.")
                        Spacer(Modifier.height(7.dp))
                        Text("Supported Units:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Meters (m) - SI base unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Feet (ft) - Imperial unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Inches (in) - Imperial unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Kilometers (km) - Metric",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("• Miles (mi) - Imperial", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "• Nautical Miles (nmi) - Aviation/Marine",
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
            Icons.Default.Straighten,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter a value in any length unit",
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
                    meters.isBlank() && feet.isBlank() && inches.isBlank() && kilometers.isBlank() && miles.isBlank() && nauticalMiles.isBlank()

                // Meters
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "m") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = meters,
                        onValueChange = {
                            meters = it
                            lastEdited = "m"
                            updateFromMeters(it)
                        },
                        label = { Text("Meters (m)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "m" || meters.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Feet
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "ft") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = feet,
                        onValueChange = {
                            feet = it
                            lastEdited = "ft"
                            updateFromFeet(it)
                        },
                        label = { Text("Feet (ft)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "ft" || feet.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Inches
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "in") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = inches,
                        onValueChange = {
                            inches = it
                            lastEdited = "in"
                            updateFromInches(it)
                        },
                        label = { Text("Inches (in)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "in" || inches.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Kilometers
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "km") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = kilometers,
                        onValueChange = {
                            kilometers = it
                            lastEdited = "km"
                            updateFromKilometers(it)
                        },
                        label = { Text("Kilometers (km)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "km" || kilometers.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Miles
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "mi") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = miles,
                        onValueChange = {
                            miles = it
                            lastEdited = "mi"
                            updateFromMiles(it)
                        },
                        label = { Text("Miles (mi)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "mi" || miles.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Nautical Miles
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "nmi") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = nauticalMiles,
                        onValueChange = {
                            nauticalMiles = it
                            lastEdited = "nmi"
                            updateFromNauticalMiles(it)
                        },
                        label = { Text("Nautical Miles (nmi)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "nmi" || nauticalMiles.isEmpty(),
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