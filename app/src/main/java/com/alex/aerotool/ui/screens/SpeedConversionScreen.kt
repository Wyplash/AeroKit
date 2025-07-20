package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun SpeedConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var knots by remember { mutableStateOf("") }
    var mph by remember { mutableStateOf("") }
    var kmh by remember { mutableStateOf("") }
    var mps by remember { mutableStateOf("") }
    var fps by remember { mutableStateOf("") }
    var mach by remember { mutableStateOf("") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Conversion functions
    fun updateFromKnots(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            mph = (value * 1.15078).round(2)
            kmh = (value * 1.852).round(2)
            mps = (value * 0.514444).round(2)
            fps = (value * 1.68781).round(2)
            mach = (value / 661.47).round(4)
        } else {
            mph = ""; kmh = ""; mps = ""; fps = ""; mach = ""
        }
    }

    fun updateFromMph(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            knots = (value / 1.15078).round(2)
            kmh = (value * 1.60934).round(2)
            mps = (value * 0.44704).round(2)
            fps = (value * 1.46667).round(2)
            mach = (value / 761.207).round(4)
        } else {
            knots = ""; kmh = ""; mps = ""; fps = ""; mach = ""
        }
    }

    fun updateFromKmh(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            knots = (value / 1.852).round(2)
            mph = (value / 1.60934).round(2)
            mps = (value / 3.6).round(2)
            fps = (value * 0.911344).round(2)
            mach = (value / 1234.8).round(4)
        } else {
            knots = ""; mph = ""; mps = ""; fps = ""; mach = ""
        }
    }

    fun updateFromMps(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            knots = (value / 0.514444).round(2)
            mph = (value / 0.44704).round(2)
            kmh = (value * 3.6).round(2)
            fps = (value * 3.28084).round(2)
            mach = (value / 343.0).round(4)
        } else {
            knots = ""; mph = ""; kmh = ""; fps = ""; mach = ""
        }
    }

    fun updateFromFps(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            knots = (value / 1.68781).round(2)
            mph = (value / 1.46667).round(2)
            kmh = (value / 0.911344).round(2)
            mps = (value / 3.28084).round(2)
            mach = (value / 1125.33).round(4)
        } else {
            knots = ""; mph = ""; kmh = ""; mps = ""; mach = ""
        }
    }

    fun updateFromMach(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            knots = (value * 661.47).round(2)
            mph = (value * 761.207).round(2)
            kmh = (value * 1234.8).round(2)
            mps = (value * 343.0).round(2)
            fps = (value * 1125.33).round(2)
        } else {
            knots = ""; mph = ""; kmh = ""; mps = ""; fps = ""
        }
    }

    fun clearAll() {
        knots = ""; mph = ""; kmh = ""; mps = ""; fps = ""; mach = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Speed Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a value in any speed unit. The other fields update instantly.")
                        Spacer(Modifier.height(7.dp))
                        Text("Supported Units:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Knots (kt) - Aviation standard",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("• Miles per Hour (mph)", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "• Kilometers per Hour (km/h)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Meters per Second (m/s)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("• Feet per Second (ft/s)", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "• Mach Number (at sea level)",
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
            Icons.Default.Speed,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter a value in any speed unit",
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
                    knots.isBlank() && mph.isBlank() && kmh.isBlank() && mps.isBlank() && fps.isBlank() && mach.isBlank()

                // Knots
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "kt") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = knots,
                        onValueChange = {
                            knots = it
                            lastEdited = "kt"
                            updateFromKnots(it)
                        },
                        label = { Text("Knots (kt)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "kt" || knots.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // MPH
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "mph") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = mph,
                        onValueChange = {
                            mph = it
                            lastEdited = "mph"
                            updateFromMph(it)
                        },
                        label = { Text("Miles per Hour (mph)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "mph" || mph.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // KM/H
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "kmh") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = kmh,
                        onValueChange = {
                            kmh = it
                            lastEdited = "kmh"
                            updateFromKmh(it)
                        },
                        label = { Text("Kilometers per Hour (km/h)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "kmh" || kmh.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // M/S
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "mps") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = mps,
                        onValueChange = {
                            mps = it
                            lastEdited = "mps"
                            updateFromMps(it)
                        },
                        label = { Text("Meters per Second (m/s)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "mps" || mps.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // FT/S
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "fps") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = fps,
                        onValueChange = {
                            fps = it
                            lastEdited = "fps"
                            updateFromFps(it)
                        },
                        label = { Text("Feet per Second (ft/s)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "fps" || fps.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Mach
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "mach") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = mach,
                        onValueChange = {
                            mach = it
                            lastEdited = "mach"
                            updateFromMach(it)
                        },
                        label = { Text("Mach Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "mach" || mach.isEmpty(),
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