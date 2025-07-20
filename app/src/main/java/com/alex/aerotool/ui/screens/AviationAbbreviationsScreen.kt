package com.alex.aerotool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alex.aerotool.ui.components.AeroTopBar
import com.alex.aerotool.ui.components.CalculatorFabAndSheet
import com.alex.aerotool.ui.theme.AppLanguage
import com.alex.aerotool.ui.theme.ThemeController

data class AbbreviationItem(
    val abbreviation: String,
    val fullForm: String,
    val fullFormFrench: String
)

@Composable
fun AviationAbbreviationsScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    customAbbreviations: List<AbbreviationItem>,
    setCustomAbbreviations: (List<AbbreviationItem>) -> Unit
) {
    val lang = themeController.language
    var showInfo by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Remember a mutable abbreviations list so user can add during session
    val builtInAbbreviations = remember {
        listOf(
        // Air Traffic Control
            AbbreviationItem("ATC", "Air Traffic Control", "Contrôle du trafic aérien"),
            AbbreviationItem("TWR", "Tower", "Tour de contrôle"),
            AbbreviationItem("APP", "Approach", "Approche"),
            AbbreviationItem("DEP", "Departure", "Départ"),
            AbbreviationItem("GND", "Ground", "Sol"),
            AbbreviationItem("CLR", "Clearance", "Autorisation"),
            AbbreviationItem("CTR", "Control Zone", "Zone de contrôle"),
            AbbreviationItem("TMA", "Terminal Control Area", "Zone de contrôle terminal"),
            AbbreviationItem("FIR", "Flight Information Region", "Région d'information de vol"),
            AbbreviationItem("UIR", "Upper Information Region", "Région d'information supérieure"),
            AbbreviationItem("ASR", "Airport Surveillance Radar", "ASR"),
            AbbreviationItem("SSR", "Secondary Surveillance Radar", "SSR"),
            AbbreviationItem("DF", "Direction Finder", "DF"),
            AbbreviationItem("GCA", "Ground Controlled Approach", "GCA"),
            AbbreviationItem("SELCAL", "Selective Calling", "SELCAL"),
        // Flight Operations & General
            AbbreviationItem("IFR", "Instrument Flight Rules", "Règles de vol aux instruments"),
            AbbreviationItem("VFR", "Visual Flight Rules", "Règles de vol à vue"),
            AbbreviationItem(
                "VMC",
                "Visual Meteorological Conditions",
                "Conditions météorologiques de vol à vue"
            ),
            AbbreviationItem(
                "IMC",
                "Instrument Meteorological Conditions",
                "Conditions météorologiques de vol aux instruments"
            ),
            AbbreviationItem("ETA", "Estimated Time of Arrival", "Heure estimée d'arrivée"),
            AbbreviationItem("ETD", "Estimated Time of Departure", "Heure estimée de départ"),
            AbbreviationItem("ATA", "Actual Time of Arrival", "Heure réelle d'arrivée"),
            AbbreviationItem("ATD", "Actual Time of Departure", "Heure réelle de départ"),
            AbbreviationItem(
                "SID",
                "Standard Instrument Departure",
                "Départ standard aux instruments"
            ),
            AbbreviationItem("STAR", "Standard Terminal Arrival Route", "Route d'arrivée standard"),
            AbbreviationItem("FPL", "Flight Plan", "Plan de vol"),
            AbbreviationItem("PNR", "Point of No Return", "Point de non-retour"),
            AbbreviationItem("PIC", "Pilot in Command", "Commandant de bord"),
            AbbreviationItem("OPS", "Operations", "Operations"),
            AbbreviationItem("AIM", "Aeronautical Information Manual", "AIM"),
            AbbreviationItem(
                "SOP",
                "Standard Operating Procedure",
                "Procédure opérationnelle normalisée"
            ),
            AbbreviationItem("MTOW", "Maximum Takeoff Weight", "Masse maximale au décollage"),
            AbbreviationItem("MAC", "Mean Aerodynamic Chord", "Corde aérodynamique moyenne"),
            AbbreviationItem("NM", "Nautical Mile", "Mille nautique"),
            AbbreviationItem("KT", "Knots", "Nœuds"),
            AbbreviationItem("FT", "Feet", "Pieds"),
            AbbreviationItem("LBS", "Pounds", "Livres"),
            AbbreviationItem("KG", "Kilograms", "Kilogrammes"),
        // Weather
            AbbreviationItem(
                "METAR",
                "Meteorological Aerodrome Report",
                "Rapport météorologique d'aérodrome"
            ),
            AbbreviationItem("TAF", "Terminal Aerodrome Forecast", "Prévision d'aérodrome"),
            AbbreviationItem(
                "ATIS",
                "Automatic Terminal Information Service",
                "Service automatique d'information terminal"
            ),
            AbbreviationItem("CAVOK", "Ceiling and Visibility OK", "Plafond et visibilité OK"),
            AbbreviationItem("SKC", "Sky Clear", "Ciel dégagé"),
            AbbreviationItem("FEW", "Few Clouds (1-2 oktas)", "Quelques nuages (1-2 octas)"),
            AbbreviationItem("SCT", "Scattered Clouds (3-4 oktas)", "Nuages épars (3-4 octas)"),
            AbbreviationItem("BKN", "Broken Clouds (5-7 oktas)", "Nuages fragmentés (5-7 octas)"),
            AbbreviationItem("OVC", "Overcast (8 oktas)", "Couvert (8 octas)"),
            AbbreviationItem("RA", "Rain", "Pluie"),
            AbbreviationItem("SN", "Snow", "Neige"),
            AbbreviationItem("FG", "Fog", "Brouillard"),
            AbbreviationItem("BR", "Mist", "Brume"),
            AbbreviationItem("TS", "Thunderstorm", "Orage"),
            AbbreviationItem("AWOS", "Automated Weather Observing System", "AWOS"),
            AbbreviationItem("VOLMET", "Meteorological Info for Aircraft in Flight", "VOLMET"),
        // Navigation & Instruments
            AbbreviationItem("VOR", "VHF Omnidirectional Range", "Radiophare omnidirectionnel VHF"),
            AbbreviationItem(
                "DME",
                "Distance Measuring Equipment",
                "Équipement de mesure de distance"
            ),
            AbbreviationItem(
                "ILS",
                "Instrument Landing System",
                "Système d'atterrissage aux instruments"
            ),
            AbbreviationItem(
                "GPS",
                "Global Positioning System",
                "Système de positionnement global"
            ),
            AbbreviationItem("NDB", "Non-Directional Beacon", "Radiophare non directionnel"),
            AbbreviationItem("ADF", "Automatic Direction Finder", "Radiogoniomètre automatique"),
            AbbreviationItem("RNAV", "Area Navigation", "Navigation de surface"),
            AbbreviationItem("TACAN", "Tactical Air Navigation", "Navigation aérienne tactique"),
            AbbreviationItem(
                "GNSS",
                "Global Navigation Satellite System",
                "Système global de navigation par satellite"
            ),
            AbbreviationItem(
                "PBN",
                "Performance Based Navigation",
                "Navigation basée sur les performances"
            ),
            AbbreviationItem("RNP", "Required Navigation Performance", "RNP"),
            AbbreviationItem("CVR", "Cockpit Voice Recorder", "CVR"),
            AbbreviationItem(
                "ACARS",
                "Aircraft Communications Addressing and Reporting System",
                "ACARS"
            ),
            AbbreviationItem("HF", "High Frequency", "HF"),
            AbbreviationItem("UHF", "Ultra High Frequency", "UHF"),
            AbbreviationItem("VHF", "Very High Frequency", "VHF"),
            AbbreviationItem("ALS", "Approach Lighting System", "ALS"),
            AbbreviationItem("OM", "Outer Marker", "OM"),
            AbbreviationItem("GP", "Glide Path", "GP"),
            AbbreviationItem("LOC", "Localizer", "LOC"),
            AbbreviationItem("LLZ", "Localizer", "LLZ"),
        // Aircraft Systems
            AbbreviationItem("APU", "Auxiliary Power Unit", "Groupe auxiliaire de puissance"),
            AbbreviationItem(
                "TCAS",
                "Traffic Collision Avoidance System",
                "Système d'évitement de collision"
            ),
            AbbreviationItem(
                "GPWS",
                "Ground Proximity Warning System",
                "Système d'alerte de proximité du sol"
            ),
            AbbreviationItem("FMS", "Flight Management System", "Système de gestion de vol"),
            AbbreviationItem(
                "EFIS",
                "Electronic Flight Instrument System",
                "Système d'instruments de vol électronique"
            ),
            AbbreviationItem(
                "ECAM",
                "Electronic Centralized Aircraft Monitor",
                "Moniteur électronique centralisé d'aéronef"
            ),
            AbbreviationItem(
                "EICAS",
                "Engine Indication and Crew Alerting System",
                "Système d'indication moteur et d'alerte équipage"
            ),
            AbbreviationItem("PFD", "Primary Flight Display", "Affichage primaire de vol"),
            AbbreviationItem("MFD", "Multi-Function Display", "Affichage multifonction"),
            AbbreviationItem("HUD", "Head-Up Display", "Affichage tête haute"),
            AbbreviationItem("MEL", "Minimum Equipment List", "MEL"),
            AbbreviationItem("SBY", "Standby", "SBY"),
            AbbreviationItem("STBY", "Standby", "STBY"),
            AbbreviationItem(
                "ETOPS",
                "Extended-range Twin-engine Operational Performance Standards",
                "ETOPS"
            ),
            AbbreviationItem("OAT", "Outside Air Temperature", "OAT"),
            AbbreviationItem("ZFW", "Zero Fuel Weight", "ZFW"),
            AbbreviationItem("NOTOC", "Notification To Captain", "NOTOC"),
        // Airport Operations
            AbbreviationItem(
                "ICAO",
                "International Civil Aviation Organization",
                "Organisation de l'aviation civile internationale"
            ),
            AbbreviationItem(
                "IATA",
                "International Air Transport Association",
                "Association du transport aérien international"
            ),
            AbbreviationItem("NOTAM", "Notice to Airmen", "Avis aux navigants"),
            AbbreviationItem(
                "AIP",
                "Aeronautical Information Publication",
                "Publication d'information aéronautique"
            ),
            AbbreviationItem(
                "AIRAC",
                "Aeronautical Information Regulation and Control",
                "Régulation et contrôle de l'information aéronautique"
            ),
            AbbreviationItem(
                "PAPI",
                "Precision Approach Path Indicator",
                "Indicateur de trajectoire d'approche de précision"
            ),
            AbbreviationItem(
                "VASI",
                "Visual Approach Slope Indicator",
                "Indicateur visuel de pente d'approche"
            ),
            AbbreviationItem("RVR", "Runway Visual Range", "Portée visuelle de piste"),
            AbbreviationItem(
                "LDA",
                "Landing Distance Available",
                "Distance d'atterrissage disponible"
            ),
            AbbreviationItem(
                "TORA",
                "Take-Off Run Available",
                "Distance de roulement au décollage disponible"
            ),
            AbbreviationItem(
                "TODA",
                "Takeoff Distance Available",
                "Distance de décollage disponible"
            ),
            AbbreviationItem("MCA", "Minimum Crossing Altitude", "MCA"),
            AbbreviationItem("MEA", "Minimum Enroute Altitude", "MEA"),
            AbbreviationItem("MOC", "Minimum Obstacle Clearance", "MOC"),
            AbbreviationItem("LOFT", "Line Oriented Flight Training", "LOFT"),
            AbbreviationItem("LVP", "Low Visibility Procedure", "LVP"),
        // Altimetry & Additional Common Abbreviations
            AbbreviationItem(
                "QNH",
                "Altimeter Setting (Sea Level)",
                "Calage altimétrique (niveau mer)"
            ),
            AbbreviationItem(
                "QFE",
                "Altimeter Setting (Aerodrome Level)",
                "Calage altimétrique (niveau aérodrome)"
            ),
            AbbreviationItem("QNE", "Standard Altimeter Setting", "Calage altimétrique standard"),
            AbbreviationItem("FL", "Flight Level", "Niveau de vol"),
            AbbreviationItem("MSL", "Mean Sea Level", "Niveau moyen de la mer"),
            AbbreviationItem("AGL", "Above Ground Level", "Au-dessus du niveau du sol"),
            AbbreviationItem("AMSL", "Above Mean Sea Level", "Au-dessus du niveau moyen de la mer"),
        // More
            AbbreviationItem("FBO", "Fixed Base Operator", "FBO"),
            AbbreviationItem("FOD", "Foreign Object Debris/Damage", "FOD"),
            AbbreviationItem("UTC", "Coordinated Universal Time", "Temps universel coordonné"),
            AbbreviationItem("LT", "Local Time", "Heure locale"),
            AbbreviationItem("PAX", "Passengers", "Passagers"),
            AbbreviationItem("A/C", "Aircraft", "Aéronef"),
            AbbreviationItem("ACFT", "Aircraft", "Aéronef"),
            AbbreviationItem("RPM", "Revolutions Per Minute", "Tours par minute"),
            AbbreviationItem("PWR", "Power", "Puissance"),
            AbbreviationItem("DR", "Dead Reckoning", "DR"),
            AbbreviationItem("AMS", "Aeronautical Mobile Service", "AMS"),
            AbbreviationItem("CS", "Commercial Standard", "CS"),
            AbbreviationItem("IRC", "International Route Chart", "IRC"),
            AbbreviationItem("SPL", "Supplemental Flight Plan", "SPL"),
            AbbreviationItem("MER", "Maximum Endurance Range", "MER"),
            AbbreviationItem("MOS", "Military Operating Surface", "MOS"),
            AbbreviationItem("TFC", "Traffic", "TFC"),
            AbbreviationItem("PA", "Public Address/Precision Approach", "PA"),
            AbbreviationItem("DF", "Direction Finder", "DF"),
            AbbreviationItem("STBY", "Standby", "STBY"),
            AbbreviationItem("PTT", "Push To Talk", "PTT"),
            AbbreviationItem("SBY", "Standby", "SBY"),
            AbbreviationItem("ADS-B", "Automatic Dependent Surveillance—Broadcast", "ADS-B"),
        )
    }

    // FAB add dialog state
    var abbrField by remember { mutableStateOf("") }
    var fullFormENField by remember { mutableStateOf("") }
    var fullFormFRField by remember { mutableStateOf("") }

    // Show info dialog when requested
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            title = { Text("Aviation Abbreviations") },
            text = {
                Text(
                    when (lang) {
                        AppLanguage.ENGLISH ->
                            "A comprehensive reference of aviation abbreviations used in:\n" +
                                    "• Air Traffic Control (ATC)\n" +
                                    "• Flight Operations\n" +
                                    "• Weather Reports (METAR/TAF)\n" +
                                    "• Navigation & Instruments\n" +
                                    "• Aircraft Systems\n" +
                                    "• Airport Operations\n\n" +
                                    "Use the search function to quickly find specific abbreviations.\n\nYou may also add your own abbreviations using the '+' button."

                        AppLanguage.FRENCH ->
                            "Une référence complète des abréviations aéronautiques utilisées dans :\n" +
                                    "• Contrôle du trafic aérien (ATC)\n" +
                                    "• Opérations de vol\n" +
                                    "• Rapports météo (METAR/TAF)\n" +
                                    "• Navigation et instruments\n" +
                                    "• Systèmes d'aéronef\n" +
                                    "• Opérations aéroportuaires\n\n" +
                                    "Utilisez la fonction de recherche pour trouver rapidement des abréviations spécifiques.\n\nVous pouvez aussi ajouter vos propres abréviations avec le bouton '+'."
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

    // Show Add Custom Abbreviation dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Abbreviation") },
            text = {
                Column(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = abbrField,
                        onValueChange = { abbrField = it.take(10) },
                        label = { Text("Abbreviation") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fullFormENField,
                        onValueChange = { fullFormENField = it },
                        label = { Text("Full Form (EN)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fullFormFRField,
                        onValueChange = { fullFormFRField = it },
                        label = { Text("Full Form (FR) - optional") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (abbrField.isNotBlank() && fullFormENField.isNotBlank()) {
                            setCustomAbbreviations(
                                customAbbreviations + AbbreviationItem(
                                    abbreviation = abbrField.trim(),
                                    fullForm = fullFormENField.trim(),
                                    fullFormFrench = if (fullFormFRField.isNotBlank()) fullFormFRField.trim() else fullFormENField.trim()
                                )
                            )
                            abbrField = ""
                            fullFormENField = ""
                            fullFormFRField = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val allAbbreviations = builtInAbbreviations + customAbbreviations
    val sortedAbbreviations = allAbbreviations.sortedBy { it.abbreviation.uppercase() }
    val filteredAbbreviations = if (searchQuery.isBlank()) {
        sortedAbbreviations
    } else {
        sortedAbbreviations.filter {
            it.abbreviation.contains(searchQuery, ignoreCase = true) ||
                    it.fullForm.contains(searchQuery, ignoreCase = true) ||
                    (lang == AppLanguage.FRENCH && it.fullFormFrench.contains(
                        searchQuery,
                        ignoreCase = true
                    ))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with back button, info button, and ADD button on right
            AeroTopBar(
                title = "AeroTool",
                onBackClick = onBack,
                onInfoClick = { showInfo = true },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Abbreviation")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = when (lang) {
                        AppLanguage.ENGLISH -> "Aviation Abbreviations"
                        AppLanguage.FRENCH -> "Abréviations Aéronautiques"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = {
                        Text(
                            when (lang) {
                            AppLanguage.ENGLISH -> "Search abbreviations..."
                            AppLanguage.FRENCH -> "Rechercher des abréviations..."
                        }
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredAbbreviations) { item ->
                        AbbreviationCard(
                            item = item,
                            language = lang
                        )
                    }
                }
            }
        }
        CalculatorFabAndSheet()
    }
}

@Composable
private fun AbbreviationCard(
    item: AbbreviationItem,
    language: AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.abbreviation,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(60.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (language) {
                            AppLanguage.ENGLISH -> item.fullForm
                            AppLanguage.FRENCH -> item.fullFormFrench
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}