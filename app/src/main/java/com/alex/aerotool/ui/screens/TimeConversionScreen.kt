package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun TimeConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var standardTime by remember { mutableStateOf("") }
    var decimalHours by remember { mutableStateOf("") }
    var totalMinutes by remember { mutableStateOf("") }
    var totalHours by remember { mutableStateOf("") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Convert standard time (HH:MM) to decimal hours using aviation table
    fun standardTimeToDecimal(time: String): Double? {
        try {
            val parts = time.split(":")
            if (parts.size != 2) return null
            val hours = parts[0].toIntOrNull() ?: return null
            val minutes = parts[1].toIntOrNull() ?: return null
            if (minutes >= 60) return null

            // Aviation decimal time conversion table
            val decimalMinutes = when (minutes) {
                in 0..2 -> 0.0
                in 3..8 -> 0.1
                in 9..14 -> 0.2
                in 15..20 -> 0.3
                in 21..26 -> 0.4
                in 27..32 -> 0.5
                in 33..38 -> 0.6
                in 39..44 -> 0.7
                in 45..50 -> 0.8
                in 51..56 -> 0.9
                in 57..59 -> 1.0
                else -> 0.0
            }

            return hours + decimalMinutes
        } catch (e: Exception) {
            return null
        }
    }

    // Convert decimal hours to standard time using aviation table
    fun decimalToStandardTime(decimal: Double): String {
        val hours = decimal.toInt()
        val decimalPart = decimal - hours

        // Convert decimal back to minutes - use original minute representation
        val minutes = when (decimalPart) {
            0.0 -> 0   // Could be 0, 1, or 2 - use 0 as default
            0.1 -> 6   // Representative of 3-8 range
            0.2 -> 12  // Representative of 9-14 range  
            0.3 -> 18  // Representative of 15-20 range
            0.4 -> 24  // Representative of 21-26 range
            0.5 -> 30  // Representative of 27-32 range
            0.6 -> 36  // Representative of 33-38 range
            0.7 -> 42  // Representative of 39-44 range
            0.8 -> 48  // Representative of 45-50 range
            0.9 -> 54  // Representative of 51-56 range
            1.0 -> 59  // Representative of 57-60 range
            else -> ((decimalPart * 60).toInt()) // Fallback for any other values
        }

        return String.format("%02d:%02d", hours, minutes)
    }

    // Convert minutes to HH:MM format - keep original minutes
    fun minutesToTime(totalMinutes: Double): String {
        val hours = (totalMinutes / 60).toInt()
        val minutes = (totalMinutes % 60).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }

    // Update from standard time (HH:MM)
    fun updateFromStandardTime(text: String) {
        val decimal = standardTimeToDecimal(text)
        if (decimal != null) {
            decimalHours = decimal.toString()
            val minutes = decimal * 60
            totalMinutes = minutes.toInt().toString()
            totalHours = decimal.toString()
        } else {
            decimalHours = ""; totalMinutes = ""; totalHours = ""
        }
    }

    // Update from decimal hours
    fun updateFromDecimal(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            standardTime = decimalToStandardTime(value)
            totalMinutes = (value * 60).toInt().toString()
            totalHours = value.toString()
        } else {
            standardTime = ""; totalMinutes = ""; totalHours = ""
        }
    }

    // Update from total minutes
    fun updateFromMinutes(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            val hours = (value / 60).toInt()
            val minutes = (value % 60).toInt()
            standardTime = String.format("%02d:%02d", hours, minutes)
            decimalHours = standardTimeToDecimal(standardTime).toString()
            totalHours = (value / 60).toString()
        } else {
            standardTime = ""; decimalHours = ""; totalHours = ""
        }
    }

    fun clearAll() {
        standardTime = ""; decimalHours = ""; totalMinutes = ""; totalHours = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Aviation Time Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Convert between standard time formats and decimal time used in aviation calculations.")
                        Spacer(Modifier.height(7.dp))
                        Text(
                            "Aviation Decimal Time Table:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            "• 00-02 min = .0    • 03-08 min = .1    • 09-14 min = .2",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• 15-20 min = .3    • 21-26 min = .4    • 27-32 min = .5",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• 33-38 min = .6    • 39-44 min = .7    • 45-50 min = .8",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• 51-56 min = .9    • 57-60 min = 1.0",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(7.dp))
                        Text("Examples:", style = MaterialTheme.typography.labelLarge)
                        Text("• 1:15 = 1.3 hours", style = MaterialTheme.typography.bodySmall)
                        Text("• 2:45 = 2.8 hours", style = MaterialTheme.typography.bodySmall)
                        Text("• 0:30 = 0.5 hours", style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { onInfoDismiss?.invoke() }) { Text("OK") }
                }
            )
        }

        Spacer(Modifier.height(10.dp))
        Icon(
            Icons.Default.AccessTime,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Convert between time formats",
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
                    standardTime.isBlank() && decimalHours.isBlank() && totalMinutes.isBlank() && totalHours.isBlank()

                // Standard Time (HH:MM)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "std") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = standardTime,
                        onValueChange = {
                            standardTime = it
                            lastEdited = "std"
                            updateFromStandardTime(it)
                        },
                        label = { Text("Standard Time (HH:MM)") },
                        placeholder = { Text("1:30") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "std" || standardTime.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Decimal Hours
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dec") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = decimalHours,
                        onValueChange = {
                            decimalHours = it
                            lastEdited = "dec"
                            updateFromDecimal(it)
                        },
                        label = { Text("Decimal Hours") },
                        placeholder = { Text("1.5") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "dec" || decimalHours.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Total Minutes
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "min") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = totalMinutes,
                        onValueChange = {
                            totalMinutes = it
                            lastEdited = "min"
                            updateFromMinutes(it)
                        },
                        label = { Text("Total Minutes") },
                        placeholder = { Text("90") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "min" || totalMinutes.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Total Hours
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "total") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = totalHours,
                        onValueChange = {
                            totalHours = it
                            lastEdited = "total"
                            val value = it.toDoubleOrNull()
                            if (value != null) {
                                standardTime = decimalToStandardTime(value)
                                totalMinutes = (value * 60).toInt().toString()
                                decimalHours = value.toString()
                            } else {
                                standardTime = ""; totalMinutes = ""; decimalHours = ""
                            }
                        },
                        label = { Text("Total Hours") },
                        placeholder = { Text("1.5") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "total" || totalHours.isEmpty(),
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