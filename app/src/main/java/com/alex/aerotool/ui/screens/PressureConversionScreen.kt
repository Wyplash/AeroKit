package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.alex.aerotool.ui.theme.ThemeController

@Composable
fun PressureConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var hPa by remember { mutableStateOf("") }
    var inHg by remember { mutableStateOf("") }
    var mmHg by remember { mutableStateOf("") }
    var atm by remember { mutableStateOf("") }
    var psi by remember { mutableStateOf("") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Core update logic
    fun updateFromHpa(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            inHg = (value * 0.0295299830714).round(4)
            mmHg = (value * 0.750061683).round(2)
            atm = (value * 0.000986923).round(5)
            psi = (value * 0.0145038).round(4)
        } else {
            inHg = ""; mmHg = ""; atm = ""; psi = ""
        }
    }

    fun updateFromInHg(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            hPa = (value * 33.8639).round(2)
            mmHg = (value * 25.4).round(2)
            atm = (value * 0.0334211).round(5)
            psi = (value * 0.491154).round(4)
        } else {
            hPa = ""; mmHg = ""; atm = ""; psi = ""
        }
    }

    fun updateFromMmHg(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            hPa = (value * 1.33322).round(2)
            inHg = (value * 0.0393701).round(4)
            atm = (value * 0.00131579).round(5)
            psi = (value * 0.0193368).round(4)
        } else {
            hPa = ""; inHg = ""; atm = ""; psi = ""
        }
    }

    fun updateFromAtm(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            hPa = (value * 1013.25).round(2)
            inHg = (value * 29.9213).round(4)
            mmHg = (value * 760.0).round(2)
            psi = (value * 14.6959).round(4)
        } else {
            hPa = ""; inHg = ""; mmHg = ""; psi = ""
        }
    }

    fun updateFromPsi(text: String) {
        val value = text.toDoubleOrNull()
        if (value != null) {
            hPa = (value * 68.9476).round(2)
            inHg = (value * 2.03602).round(4)
            mmHg = (value * 51.7149).round(2)
            atm = (value * 0.06804596).round(5)
        } else {
            hPa = ""; inHg = ""; mmHg = ""; atm = ""
        }
    }

    fun clearAll() {
        hPa = ""; inHg = ""; mmHg = ""; atm = ""; psi = ""
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Pressure Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Convert between common aviation pressure units.")
                        Spacer(Modifier.height(7.dp))
                        Text("Conversion Factors:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• 1 hPa = 0.02953 inHg = 0.75006 mmHg = 0.000987 atm = 0.01450 psi",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("• 1 inHg = 33.8639 hPa", style = MaterialTheme.typography.bodySmall)
                        Text("• 1 mmHg = 1.33322 hPa", style = MaterialTheme.typography.bodySmall)
                        Text("• 1 atm = 1013.25 hPa", style = MaterialTheme.typography.bodySmall)
                        Text("• 1 psi = 68.9476 hPa", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tip: Enter a value in any field.",
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
            Icons.Default.Compress,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter a pressure value in any unit",
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
                    hPa.isBlank() && inHg.isBlank() && mmHg.isBlank() && atm.isBlank() && psi.isBlank()

                // hPa
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "hpa") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = hPa,
                        onValueChange = {
                            hPa = it
                            lastEdited = "hpa"
                            updateFromHpa(it)
                        },
                        label = { Text("Hectopascal (hPa)") },
                        placeholder = { Text("1013.25") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "hpa" || hPa.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                // inHg
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "inhg") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = inHg,
                        onValueChange = {
                            inHg = it
                            lastEdited = "inhg"
                            updateFromInHg(it)
                        },
                        label = { Text("Inches of Mercury (inHg)") },
                        placeholder = { Text("29.92") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "inhg" || inHg.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                // mmHg
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "mmhg") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = mmHg,
                        onValueChange = {
                            mmHg = it
                            lastEdited = "mmhg"
                            updateFromMmHg(it)
                        },
                        label = { Text("Millimeters of Mercury (mmHg)") },
                        placeholder = { Text("760") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "mmhg" || mmHg.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                // atm
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "atm") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = atm,
                        onValueChange = {
                            atm = it
                            lastEdited = "atm"
                            updateFromAtm(it)
                        },
                        label = { Text("Standard Atmosphere (atm)") },
                        placeholder = { Text("1") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "atm" || atm.isEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White
                        )
                    )
                }
                // psi
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "psi") Color(
                            0xFF223372
                        ) else Color(0xFF117449)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = psi,
                        onValueChange = {
                            psi = it
                            lastEdited = "psi"
                            updateFromPsi(it)
                        },
                        label = { Text("Pound per Square Inch (psi)") },
                        placeholder = { Text("14.7") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        enabled = lastEdited == "psi" || psi.isEmpty(),
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
