package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.theme.AppLanguage
import com.alex.aerotool.ui.theme.ThemeController

data class PhoneticItem(
    val letter: String,
    val phonetic: String,
    val phoneticFrench: String,
    val morse: String
)

@Composable
fun PhoneticAlphabetScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null
) {
    val lang = themeController.language
    var showInfo by remember { mutableStateOf(false) }

    // Show info dialog when requested
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("Phonetic Alphabet Tool") },
            text = {
                Text(
                    when (lang) {
                        AppLanguage.ENGLISH ->
                            "The NATO phonetic alphabet (also known as the ICAO phonetic alphabet) is used " +
                                    "in aviation to ensure clear communication. Each letter has a corresponding " +
                                    "phonetic word and morse code pattern. This reference helps pilots, controllers, " +
                                    "and aviation professionals communicate clearly over radio."

                        AppLanguage.FRENCH ->
                            "L'alphabet phonétique OTAN (aussi connu sous le nom d'alphabet phonétique OACI) " +
                                    "est utilisé en aviation pour assurer une communication claire. Chaque lettre a " +
                                    "un mot phonétique correspondant et un motif de code morse. Cette référence aide " +
                                    "les pilotes, contrôleurs et professionnels de l'aviation à communiquer clairement par radio."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfo = false }) {
                    Text("OK")
                }
            }
        )
    }

    val phoneticData = listOf(
        PhoneticItem("A", "Alpha", "Alpha", "·-"),
        PhoneticItem("B", "Bravo", "Bravo", "-···"),
        PhoneticItem("C", "Charlie", "Charlie", "-·-·"),
        PhoneticItem("D", "Delta", "Delta", "-··"),
        PhoneticItem("E", "Echo", "Echo", "·"),
        PhoneticItem("F", "Foxtrot", "Foxtrot", "··-·"),
        PhoneticItem("G", "Golf", "Golf", "--·"),
        PhoneticItem("H", "Hotel", "Hotel", "····"),
        PhoneticItem("I", "India", "India", "··"),
        PhoneticItem("J", "Juliet", "Juliet", "·---"),
        PhoneticItem("K", "Kilo", "Kilo", "-·-"),
        PhoneticItem("L", "Lima", "Lima", "·-··"),
        PhoneticItem("M", "Mike", "Mike", "--"),
        PhoneticItem("N", "November", "November", "-·"),
        PhoneticItem("O", "Oscar", "Oscar", "---"),
        PhoneticItem("P", "Papa", "Papa", "·--·"),
        PhoneticItem("Q", "Quebec", "Quebec", "--·-"),
        PhoneticItem("R", "Romeo", "Romeo", "·-·"),
        PhoneticItem("S", "Sierra", "Sierra", "···"),
        PhoneticItem("T", "Tango", "Tango", "-"),
        PhoneticItem("U", "Uniform", "Uniform", "··-"),
        PhoneticItem("V", "Victor", "Victor", "···-"),
        PhoneticItem("W", "Whiskey", "Whiskey", "·--"),
        PhoneticItem("X", "X-ray", "X-ray", "-··-"),
        PhoneticItem("Y", "Yankee", "Yankee", "-·--"),
        PhoneticItem("Z", "Zulu", "Zulu", "--··")
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar with back button and info button
        AeroTopBar(
            title = "AeroTool",
            onBackClick = onBack,
            onInfoClick = { showInfo = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = when (lang) {
                    AppLanguage.ENGLISH -> "NATO Phonetic Alphabet & Morse Code"
                    AppLanguage.FRENCH -> "Alphabet Phonétique OTAN & Code Morse"
                },
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(phoneticData) { item ->
                    PhoneticCard(
                        item = item,
                        language = lang
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneticCard(
    item: PhoneticItem,
    language: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letter
            Text(
                text = item.letter,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(40.dp)
            )

            // Phonetic word
            Text(
                text = when (language) {
                    AppLanguage.ENGLISH -> item.phonetic
                    AppLanguage.FRENCH -> item.phoneticFrench
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Morse code
            Text(
                text = item.morse,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.width(70.dp)
            )
        }
    }
}