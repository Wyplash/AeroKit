package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.theme.ThemeController
import kotlin.math.abs
import kotlin.math.floor

@Composable
fun GeoCoordinateScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    var decimalLat by remember { mutableStateOf("") }
    var decimalLon by remember { mutableStateOf("") }
    var dmsLat by remember { mutableStateOf("__°__'__.__\"_") }
    var dmsLon by remember { mutableStateOf("__°__'__.__\"_") }
    var dmLat by remember { mutableStateOf("__°__.____'_") }
    var dmLon by remember { mutableStateOf("__°__.____'_") }
    var lastEdited by remember { mutableStateOf<String?>(null) }

    // Convert decimal degrees to DMS format
    fun decimalToDMS(decimal: Double, isLatitude: Boolean): String {
        val abs = abs(decimal)
        val degrees = floor(abs).toInt()
        val minutesDecimal = (abs - degrees) * 60
        val minutes = floor(minutesDecimal).toInt()
        val seconds = (minutesDecimal - minutes) * 60

        val direction = if (isLatitude) {
            if (decimal >= 0) "N" else "S"
        } else {
            if (decimal >= 0) "E" else "W"
        }

        return "${degrees}°${minutes}'${seconds.round(2)}\"$direction"
    }

    // Convert decimal degrees to DM format
    fun decimalToDM(decimal: Double, isLatitude: Boolean): String {
        val abs = abs(decimal)
        val degrees = floor(abs).toInt()
        val minutes = (abs - degrees) * 60

        val direction = if (isLatitude) {
            if (decimal >= 0) "N" else "S"
        } else {
            if (decimal >= 0) "E" else "W"
        }

        return "${degrees}°${minutes.round(4)}'$direction"
    }

    // Parse DMS format to decimal
    fun dmsToDecimal(dms: String): Double? {
        try {
            // Handle pre-populated template
            if (dms == "__°__'__.__\"_" || dms == "__°__'__.__\"N" || dms == "__°__'__.__\"W") return null

            val regex = """(\d+)°(\d+)'([\d.]+)"([NSEW])""".toRegex()
            val match = regex.find(dms.trim()) ?: return null
            val (degrees, minutes, seconds, direction) = match.destructured

            var decimal = degrees.toDouble() + minutes.toDouble() / 60 + seconds.toDouble() / 3600
            if (direction in listOf("S", "W")) {
                decimal = -decimal
            }
            return decimal
        } catch (e: Exception) {
            return null
        }
    }

    // Parse DM format to decimal
    fun dmToDecimal(dm: String): Double? {
        try {
            // Handle pre-populated template
            if (dm == "__°__.____'_" || dm == "__°__.____'N" || dm == "__°__.____'W") return null

            val regex = """(\d+)°([\d.]+)'([NSEW])""".toRegex()
            val match = regex.find(dm.trim()) ?: return null
            val (degrees, minutes, direction) = match.destructured

            var decimal = degrees.toDouble() + minutes.toDouble() / 60
            if (direction in listOf("S", "W")) {
                decimal = -decimal
            }
            return decimal
        } catch (e: Exception) {
            return null
        }
    }

    // Update from decimal degrees
    fun updateFromDecimal() {
        val lat = decimalLat.toDoubleOrNull()
        val lon = decimalLon.toDoubleOrNull()

        if (lat != null && lon != null) {
            dmsLat = decimalToDMS(lat, true)
            dmsLon = decimalToDMS(lon, false)
            dmLat = decimalToDM(lat, true)
            dmLon = decimalToDM(lon, false)
        } else {
            dmsLat = "__°__'__.__\"_"; dmsLon = "__°__'__.__\"_"; dmLat = "__°__.____'_"; dmLon =
                "__°__.____'_"
        }
    }

    // Update from DMS format
    fun updateFromDMS() {
        val lat = dmsToDecimal(dmsLat)
        val lon = dmsToDecimal(dmsLon)

        if (lat != null && lon != null) {
            decimalLat = lat.round(6)
            decimalLon = lon.round(6)
            dmLat = decimalToDM(lat, true)
            dmLon = decimalToDM(lon, false)
        } else {
            decimalLat = ""; decimalLon = ""; dmLat = "__°__.____'_"; dmLon = "__°__.____'_"
        }
    }

    // Update from DM format
    fun updateFromDM() {
        val lat = dmToDecimal(dmLat)
        val lon = dmToDecimal(dmLon)

        if (lat != null && lon != null) {
            decimalLat = lat.round(6)
            decimalLon = lon.round(6)
            dmsLat = decimalToDMS(lat, true)
            dmsLon = decimalToDMS(lon, false)
        } else {
            decimalLat = ""; decimalLon = ""; dmsLat = "__°__'__.__\"_"; dmsLon = "__°__'__.__\"_"
        }
    }

    // Format DMS input - extracts digits and places them in template, leaves direction ('_') open
    fun formatDMSInput(input: String): String {
        val raw = input.filter { it.isDigit() }
        val direction = input.lastOrNull { it.uppercaseChar() in listOf('N', 'S', 'E', 'W') } ?: '_'
        return when (raw.length) {
            0 -> "__°__'__.__\"$direction"
            1 -> "${raw[0]}_°__'__.__\"$direction"
            2 -> "${raw[0]}${raw[1]}°__'__.__\"$direction"
            3 -> "${raw[0]}${raw[1]}°${raw[2]}_'__.__\"$direction"
            4 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}'__.__\"$direction"
            5 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}'${raw[4]}_.__\"$direction"
            6 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}'${raw[4]}${raw[5]}.__\"$direction"
            7 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}'${raw[4]}${raw[5]}.${raw[6]}_\"$direction"
            else -> "${raw.take(2)}°${raw.substring(2, 4)}'${raw.substring(4, 6)}.${
                raw.substring(
                    6,
                    8
                )
            }\"$direction"
        }
    }

    // Format DM input - extracts digits and places them in template, leaves direction ('_') open
    fun formatDMInput(input: String): String {
        val raw = input.filter { it.isDigit() }
        val direction = input.lastOrNull { it.uppercaseChar() in listOf('N', 'S', 'E', 'W') } ?: '_'
        return when (raw.length) {
            0 -> "__°__.____'$direction"
            1 -> "${raw[0]}_°__.____'$direction"
            2 -> "${raw[0]}${raw[1]}°__.____'$direction"
            3 -> "${raw[0]}${raw[1]}°${raw[2]}_.____'$direction"
            4 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}.____'$direction"
            5 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}.${raw[4]}___'$direction"
            6 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}.${raw[4]}${raw[5]}__'$direction"
            7 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}.${raw[4]}${raw[5]}${raw[6]}_'$direction"
            8 -> "${raw[0]}${raw[1]}°${raw[2]}${raw[3]}.${raw[4]}${raw[5]}${raw[6]}${raw[7]}'$direction"
            else -> "${raw.take(2)}°${raw.substring(2, 4)}.${raw.substring(4, 8)}'$direction"
        }
    }

    fun clearAll() {
        decimalLat = ""; decimalLon = ""; dmsLat = "__°__'__.__\"_"; dmsLon =
            "__°__'__.__\"_"; dmLat = "__°__.____'_"; dmLon =
            "__°__.____'_"
    }

    Column(Modifier.fillMaxSize()) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("About Coordinate Converter") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Convert between different coordinate formats. Enter both latitude and longitude for conversion.")
                        Spacer(Modifier.height(7.dp))
                        Text("Supported Formats:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "• Decimal Degrees (DD): 40.123456, -74.123456",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Degrees Minutes Seconds (DMS): 40°7'24.44\"N, 74°7'24.44\"W",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "• Degrees Minutes (DM): 40°7.4073'N, 74°7.4073'W",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Note: Both lat/lon must be entered for conversion to work.",
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
            Icons.Default.Explore,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            "Enter coordinates in any format",
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
                    decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_"

                // Decimal Degrees Section
                Text("Decimal Degrees", style = MaterialTheme.typography.labelMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dd") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = decimalLat,
                            onValueChange = {
                                decimalLat = it
                                lastEdited = "dd"
                                updateFromDecimal()
                            },
                            label = { Text("Latitude") },
                            placeholder = { Text("40.123456") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            enabled = lastEdited == "dd" || (decimalLat.isEmpty() && decimalLon.isEmpty()),
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
                            containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dd") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = decimalLon,
                            onValueChange = {
                                decimalLon = it
                                lastEdited = "dd"
                                updateFromDecimal()
                            },
                            label = { Text("Longitude") },
                            placeholder = { Text("-74.123456") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            enabled = lastEdited == "dd" || (decimalLat.isEmpty() && decimalLon.isEmpty()),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // DMS Section
                Text("Degrees Minutes Seconds", style = MaterialTheme.typography.labelMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dms") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = dmsLat,
                            onValueChange = {
                                dmsLat = formatDMSInput(it)
                                lastEdited = "dms"
                                updateFromDMS()
                            },
                            label = { Text("Latitude DMS") },
                            placeholder = { Text("__°__'__.__\"_") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            enabled = lastEdited == "dms" || (dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_"),
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
                            containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dms") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = dmsLon,
                            onValueChange = {
                                dmsLon = formatDMSInput(it)
                                lastEdited = "dms"
                                updateFromDMS()
                            },
                            label = { Text("Longitude DMS") },
                            placeholder = { Text("__°__'__.__\"_") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            enabled = lastEdited == "dms" || (dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // DM Section
                Text("Degrees Minutes", style = MaterialTheme.typography.labelMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dm") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = dmLat,
                            onValueChange = {
                                dmLat = formatDMInput(it)
                                lastEdited = "dm"
                                updateFromDM()
                            },
                            label = { Text("Latitude DM") },
                            placeholder = { Text("__°__.____'_") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            enabled = lastEdited == "dm" || (dmLat == "__°__.____'_" && dmLon == "__°__.____'_"),
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
                            containerColor = if (isBlank) MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dm") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = dmLon,
                            onValueChange = {
                                dmLon = formatDMInput(it)
                                lastEdited = "dm"
                                updateFromDM()
                            },
                            label = { Text("Longitude DM") },
                            placeholder = { Text("__°__.____'_") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            enabled = lastEdited == "dm" || (dmLat == "__°__.____'_" && dmLon == "__°__.____'_"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White
                            )
                        )
                    }
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