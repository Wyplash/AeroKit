package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showInfo) {
            AlertDialog(
                onDismissRequest = { onInfoDismiss?.invoke() },
                title = { Text("Geo Coordinate Tool") },
                text = {
                    Text(
                        "Convert between Degrees Minutes Seconds (DMS) and Decimal Degrees (DD). Useful for waypoint entry and mapping.\n\n" +
                                "• Enter coordinates in either DMS or DD format\n" +
                                "• Use hemisphere selectors (N/S, E/W) as needed\n" +
                                "• Tap the convert button to transform between formats\n" +
                                "• Results show both formats for cross-checking\n\n" +
                                "Example:\n" +
                                "  DMS: 45°30'15\" N 073°34'55\" W\n" +
                                "  →  DD: 45.50417°, -73.58194°"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onInfoDismiss?.invoke() }) { Text("OK") }
                }
            )
        }

        Spacer(Modifier.height(11.dp))
        Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(38.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Geo Coordinate",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Enter coordinates in any format",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Decimal Degrees Section
            item {
                Text(
                    "Decimal Degrees",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_") MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dd") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = if (lastEdited == "dd") androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outline
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "🌐",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    "Latitude",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "dd") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = decimalLat,
                                onValueChange = {
                                    decimalLat = it
                                    lastEdited = "dd"
                                },
                                placeholder = { Text("40.123456") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    updateFromDecimal()
                                }),
                                enabled = lastEdited == "dd" || (decimalLat.isEmpty() && decimalLon.isEmpty()),
                                colors = if (lastEdited == "dd") OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White
                                ) else OutlinedTextFieldDefaults.colors()
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_") MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dd") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = if (lastEdited == "dd") androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outline
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "🗺️",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    "Longitude",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "dd") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = decimalLon,
                                onValueChange = {
                                    decimalLon = it
                                    lastEdited = "dd"
                                },
                                placeholder = { Text("-74.123456") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    updateFromDecimal()
                                }),
                                enabled = lastEdited == "dd" || (decimalLat.isEmpty() && decimalLon.isEmpty()),
                                colors = if (lastEdited == "dd") OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White
                                ) else OutlinedTextFieldDefaults.colors()
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // DMS Section
            item {
                Text(
                    "Degrees Minutes Seconds",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_") MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dms") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = if (lastEdited == "dms") androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outline
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "🧭",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    "Latitude DMS",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "dms") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = dmsLat,
                                onValueChange = {
                                    dmsLat = formatDMSInput(it)
                                    lastEdited = "dms"
                                },
                                placeholder = { Text("__°__'__.__\"_") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    updateFromDMS()
                                }),
                                enabled = lastEdited == "dms" || (dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_"),
                                colors = if (lastEdited == "dms") OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White
                                ) else OutlinedTextFieldDefaults.colors()
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_") MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dms") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = if (lastEdited == "dms") androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outline
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "🎯",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    "Longitude DMS",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "dms") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = dmsLon,
                                onValueChange = {
                                    dmsLon = formatDMSInput(it)
                                    lastEdited = "dms"
                                },
                                placeholder = { Text("__°__'__.__\"_") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    updateFromDMS()
                                }),
                                enabled = lastEdited == "dms" || (dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_"),
                                colors = if (lastEdited == "dms") OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White
                                ) else OutlinedTextFieldDefaults.colors()
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }

            // DM Section
            item {
                Text(
                    "Degrees Minutes",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_") MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dm") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = if (lastEdited == "dm") androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outline
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "🎨",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    "Latitude DM",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "dm") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = dmLat,
                                onValueChange = {
                                    dmLat = formatDMInput(it)
                                    lastEdited = "dm"
                                },
                                placeholder = { Text("__°__.____'_") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    updateFromDM()
                                }),
                                enabled = lastEdited == "dm" || (dmLat == "__°__.____'_" && dmLon == "__°__.____'_"),
                                colors = if (lastEdited == "dm") OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White
                                ) else OutlinedTextFieldDefaults.colors()
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (decimalLat.isBlank() && decimalLon.isBlank() && dmsLat == "__°__'__.__\"_" && dmsLon == "__°__'__.__\"_" && dmLat == "__°__.____'_" && dmLon == "__°__.____'_") MaterialTheme.colorScheme.surfaceVariant else if (lastEdited == "dm") Color(
                                0xFF223372
                            ) else Color(0xFF117449)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = if (lastEdited == "dm") androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outline
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "📍",
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    "Longitude DM",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (lastEdited == "dm") Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = dmLon,
                                onValueChange = {
                                    dmLon = formatDMInput(it)
                                    lastEdited = "dm"
                                },
                                placeholder = { Text("__°__.____'_") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    updateFromDM()
                                }),
                                enabled = lastEdited == "dm" || (dmLat == "__°__.____'_" && dmLon == "__°__.____'_"),
                                colors = if (lastEdited == "dm") OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    disabledTextColor = Color.White
                                ) else OutlinedTextFieldDefaults.colors()
                            )
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

private fun Double.round(n: Int): String {
    val precision = n.coerceIn(0, 9)
    return "%.${precision}f".format(this)
}