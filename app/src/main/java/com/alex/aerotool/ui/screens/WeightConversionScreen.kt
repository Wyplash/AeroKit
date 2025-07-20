package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun WeightConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var kilograms by remember { mutableStateOf("") }
    var pounds by remember { mutableStateOf("") }
    var ounces by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }
    var stones by remember { mutableStateOf("") }
    var tons by remember { mutableStateOf("") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Conversion functions
    fun updateFromKilograms(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            pounds = (value * 2.20462).round(4)
            ounces = (value * 35.274).round(2)
            grams = (value * 1000).round(2)
            stones = (value * 0.157473).round(4)
            tons = (value / 1000).round(6)
        } else {
            pounds = ""; ounces = ""; grams = ""; stones = ""; tons = ""
        }
    }

    fun updateFromPounds(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            kilograms = (value / 2.20462).round(4)
            ounces = (value * 16).round(2)
            grams = (value * 453.592).round(2)
            stones = (value / 14).round(4)
            tons = (value / 2204.62).round(6)
        } else {
            kilograms = ""; ounces = ""; grams = ""; stones = ""; tons = ""
        }
    }

    fun updateFromOunces(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            kilograms = (value / 35.274).round(4)
            pounds = (value / 16).round(4)
            grams = (value * 28.3495).round(2)
            stones = (value / 224).round(4)
            tons = (value / 35274).round(6)
        } else {
            kilograms = ""; pounds = ""; grams = ""; stones = ""; tons = ""
        }
    }

    fun updateFromGrams(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            kilograms = (value / 1000).round(4)
            pounds = (value / 453.592).round(4)
            ounces = (value / 28.3495).round(2)
            stones = (value / 6350.29).round(4)
            tons = (value / 1000000).round(6)
        } else {
            kilograms = ""; pounds = ""; ounces = ""; stones = ""; tons = ""
        }
    }

    fun updateFromStones(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            kilograms = (value * 6.35029).round(4)
            pounds = (value * 14).round(4)
            ounces = (value * 224).round(2)
            grams = (value * 6350.29).round(2)
            tons = (value / 157.473).round(6)
        } else {
            kilograms = ""; pounds = ""; ounces = ""; grams = ""; tons = ""
        }
    }

    fun updateFromTons(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            kilograms = (value * 1000).round(2)
            pounds = (value * 2204.62).round(2)
            ounces = (value * 35274).round(2)
            grams = (value * 1000000).round(2)
            stones = (value * 157.473).round(4)
        } else {
            kilograms = ""; pounds = ""; ounces = ""; grams = ""; stones = ""
        }
    }

    fun clearAll() {
        kilograms = ""; pounds = ""; ounces = ""; grams = ""; stones = ""; tons = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Weight Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Enter a value in any weight unit. The other fields update instantly.")
                        Spacer(Modifier.height(7.dp))
                        Text("Supported Units:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Kilograms (kg) - SI base unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Pounds (lb) - Imperial unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Ounces (oz) - Imperial unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Grams (g) - Metric unit",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Stones (st) - British imperial",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Metric Tons (t) - 1000 kg",
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
            Icons.Default.LineWeight,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter a value in any weight unit",
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
                    kilograms.isBlank() && pounds.isBlank() && ounces.isBlank() && grams.isBlank() && stones.isBlank() && tons.isBlank()

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
                        label = { Text("Kilograms (kg)") },
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

                // Pounds
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "lb") Color(
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
                            lastEdited = "lb"
                            updateFromPounds(it)
                        },
                        label = { Text("Pounds (lb)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "lb" || pounds.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Ounces
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "oz") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = ounces,
                        onValueChange = {
                            ounces = it
                            lastEdited = "oz"
                            updateFromOunces(it)
                        },
                        label = { Text("Ounces (oz)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "oz" || ounces.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Grams
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "g") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = grams,
                        onValueChange = {
                            grams = it
                            lastEdited = "g"
                            updateFromGrams(it)
                        },
                        label = { Text("Grams (g)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "g" || grams.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Stones
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "st") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = stones,
                        onValueChange = {
                            stones = it
                            lastEdited = "st"
                            updateFromStones(it)
                        },
                        label = { Text("Stones (st)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "st" || stones.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }

                // Tons
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "t") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = tons,
                        onValueChange = {
                            tons = it
                            lastEdited = "t"
                            updateFromTons(it)
                        },
                        label = { Text("Metric Tons (t)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "t" || tons.isEmpty(),
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