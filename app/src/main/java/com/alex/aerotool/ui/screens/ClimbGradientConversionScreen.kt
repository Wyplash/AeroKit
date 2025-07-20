package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController
import kotlin.math.*

@Composable
fun ClimbGradientConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var percentage by remember { mutableStateOf("") }
    var feetPerMinute by remember { mutableStateOf("") }
    var degrees by remember { mutableStateOf("") }
    var groundSpeed by remember { mutableStateOf("100") } // Default ground speed in knots
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Convert percentage to degrees
    fun percentageToDegrees(percent: Double): Double {
        return atan(percent / 100.0) * 180.0 / PI
    }

    // Convert degrees to percentage
    fun degreesToPercentage(deg: Double): Double {
        return tan(deg * PI / 180.0) * 100.0
    }

    // Convert percentage to feet per minute (requires ground speed)
    fun percentageToFeetPerMinute(percent: Double, groundSpeedKnots: Double): Double {
        val groundSpeedFeetPerMin = groundSpeedKnots * 101.268667 // knots to feet per minute
        return (percent / 100.0) * groundSpeedFeetPerMin
    }

    // Convert feet per minute to percentage (requires ground speed)
    fun feetPerMinuteToPercentage(fpm: Double, groundSpeedKnots: Double): Double {
        val groundSpeedFeetPerMin = groundSpeedKnots * 101.268667 // knots to feet per minute
        return if (groundSpeedFeetPerMin > 0) (fpm / groundSpeedFeetPerMin) * 100.0 else 0.0
    }

    // Update from percentage
    fun updateFromPercentage(text: String) {
        val value = text.toDoubleOrNull()
        val gsValue = groundSpeed.toDoubleOrNull() ?: 100.0
        if (value != null) {
            degrees = percentageToDegrees(value).round(3)
            feetPerMinute = percentageToFeetPerMinute(value, gsValue).round(0)
        } else {
            degrees = ""; feetPerMinute = ""
        }
    }

    // Update from degrees
    fun updateFromDegrees(text: String) {
        val value = text.toDoubleOrNull()
        val gsValue = groundSpeed.toDoubleOrNull() ?: 100.0
        if (value != null) {
            val percent = degreesToPercentage(value)
            percentage = percent.round(3)
            feetPerMinute = percentageToFeetPerMinute(percent, gsValue).round(0)
        } else {
            percentage = ""; feetPerMinute = ""
        }
    }

    // Update from feet per minute
    fun updateFromFeetPerMinute(text: String) {
        val value = text.toDoubleOrNull()
        val gsValue = groundSpeed.toDoubleOrNull() ?: 100.0
        if (value != null) {
            val percent = feetPerMinuteToPercentage(value, gsValue)
            percentage = percent.round(3)
            degrees = percentageToDegrees(percent).round(3)
        } else {
            percentage = ""; degrees = ""
        }
    }

    // Update all when ground speed changes
    fun updateFromGroundSpeed() {
        when (lastEdited) {
            "%" -> updateFromPercentage(percentage)
            "deg" -> updateFromDegrees(degrees)
            "fpm" -> updateFromFeetPerMinute(feetPerMinute)
        }
    }

    fun clearAll() {
        percentage = ""; feetPerMinute = ""; degrees = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Climb Gradient Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Convert between different climb gradient formats used in aviation.")
                        Spacer(Modifier.height(7.dp))
                        Text("Conversion Formulas:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Percentage to Degrees: arctan(% / 100) × 180/π",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Degrees to Percentage: tan(degrees × π/180) × 100",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Percentage to ft/min: (% / 100) × Ground Speed (ft/min)",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Note: Ground speed is required for ft/min conversions. Default is 100 knots.",
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
            Icons.Default.TrendingUp,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter climb gradient in any format",
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
                val isBlank = percentage.isBlank() && feetPerMinute.isBlank() && degrees.isBlank()

                // Ground Speed Input
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = groundSpeed,
                        onValueChange = {
                            groundSpeed = it
                            updateFromGroundSpeed()
                        },
                        label = { Text("Ground Speed (knots)") },
                        placeholder = { Text("100") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Percentage
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "%") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = percentage,
                        onValueChange = {
                            percentage = it
                            lastEdited = "%"
                            updateFromPercentage(it)
                        },
                        label = { Text("Percentage (%)") },
                        placeholder = { Text("5.0") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "%" || percentage.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Degrees
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "deg") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = degrees,
                        onValueChange = {
                            degrees = it
                            lastEdited = "deg"
                            updateFromDegrees(it)
                        },
                        label = { Text("Degrees (°)") },
                        placeholder = { Text("2.86") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "deg" || degrees.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Feet per Minute
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "fpm") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = feetPerMinute,
                        onValueChange = {
                            feetPerMinute = it
                            lastEdited = "fpm"
                            updateFromFeetPerMinute(it)
                        },
                        label = { Text("Feet per Minute (ft/min)") },
                        placeholder = { Text("506") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "fpm" || feetPerMinute.isEmpty(),
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