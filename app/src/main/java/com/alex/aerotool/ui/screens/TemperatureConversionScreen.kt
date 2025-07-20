package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.theme.ThemeController
import kotlin.math.roundToInt

@Composable
fun TemperatureConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var celsius by remember { mutableStateOf("") }
    var fahrenheit by remember { mutableStateOf("") }
    var kelvin by remember { mutableStateOf("") }
    var rankine by remember { mutableStateOf("") }
    // Which field holds the "truth"
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Core sync logic
    fun updateFromCelsius(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            fahrenheit = ((value * 9.0 / 5.0) + 32).round(2)
            kelvin = (value + 273.15).round(2)
            rankine = ((value + 273.15) * 9.0 / 5.0).round(2)
        } else {
            fahrenheit = ""; kelvin = ""; rankine = ""
        }
    }

    fun updateFromFahrenheit(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            celsius = ((value - 32) * 5.0 / 9.0).round(2)
            kelvin = ((value + 459.67) * 5.0 / 9.0).round(2)
            rankine = (value + 459.67).round(2)
        } else {
            celsius = ""; kelvin = ""; rankine = ""
        }
    }

    fun updateFromKelvin(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            celsius = (value - 273.15).round(2)
            fahrenheit = ((value * 9.0 / 5.0) - 459.67).round(2)
            rankine = (value * 9.0 / 5.0).round(2)
        } else {
            celsius = ""; fahrenheit = ""; rankine = ""
        }
    }

    fun updateFromRankine(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            kelvin = (value * 5.0 / 9.0).round(2)
            celsius = ((value - 491.67) * 5.0 / 9.0).round(2)
            fahrenheit = (value - 459.67).round(2)
        } else {
            celsius = ""; fahrenheit = ""; kelvin = ""
        }
    }

    // Clear all
    fun clearAll() {
        celsius = ""
        fahrenheit = ""
        kelvin = ""
        rankine = ""
    }

    Column(Modifier.fillMaxSize()) {
        // Top bar is NOT shown here (parent provides it!)
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Temperature Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a value in any unit. The other fields update instantly. Supports °C, °F, K, and Rankine.")
                        Spacer(Modifier.height(7.dp))
                        Text("Conversion Formulas:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Celsius → Fahrenheit: (°C × 9/5) + 32",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Fahrenheit → Celsius: (°F − 32) × 5/9",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Celsius → Kelvin: °C + 273.15",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Kelvin → Celsius: K − 273.15",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Celsius → Rankine: (°C + 273.15) × 9/5",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Fahrenheit → Kelvin: (°F + 459.67) × 5/9",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Rankine → Kelvin: R × 5/9",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Kelvin → Fahrenheit: (K × 9/5) − 459.67",
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
            Icons.Default.DeviceThermostat,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter a value in any unit",
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
                    celsius.isBlank() && fahrenheit.isBlank() && kelvin.isBlank() && rankine.isBlank()
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "c") Color(
                            0xFF223372
                        ) else Color(0xFF117449),
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = celsius,
                        onValueChange = {
                            celsius = it
                            lastEdited = "c"
                            updateFromCelsius(it)
                        },
                        label = { Text("Celsius (°C)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "c" || celsius.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, // no dimming overlay
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "f") Color(
                            0xFF223372
                        ) else Color(0xFF117449),
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = fahrenheit,
                        onValueChange = {
                            fahrenheit = it
                            lastEdited = "f"
                            updateFromFahrenheit(it)
                        },
                        label = { Text("Fahrenheit (°F)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "f" || fahrenheit.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "k") Color(
                            0xFF223372
                        ) else Color(0xFF117449),
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = kelvin,
                        onValueChange = {
                            kelvin = it
                            lastEdited = "k"
                            updateFromKelvin(it)
                        },
                        label = { Text("Kelvin (K)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "k" || kelvin.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "r") Color(
                            0xFF223372
                        ) else Color(0xFF117449),
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rankine,
                        onValueChange = {
                            rankine = it
                            lastEdited = "r"
                            updateFromRankine(it)
                        },
                        label = { Text("Rankine (R)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "r" || rankine.isEmpty(),
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

// Rounded double to 'n' decimals and show as string
private fun Double.round(n: Int): String = "%.${n}f".format(this)