package com.alex.aerotool.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alex.aerotool.data.LanguagePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "aerotool_prefs")

@Serializable
data class Aircraft(
    val name: String,
    val crosswindLimit: Int
)

enum class AppLanguage { ENGLISH, FRENCH }

class ThemeController(
    initialLanguage: AppLanguage,
    private val languagePreference: LanguagePreference,
    private val context: Context
) {
    var language by mutableStateOf(initialLanguage)
    var decimalPrecision by mutableStateOf(2) // Default 2 decimal places

    // WindComponent persistent fields
    var runwayInput by mutableStateOf("")
    var windDirInput by mutableStateOf("")
    var windSpeedInput by mutableStateOf("")
    var crossLimitInput by mutableStateOf("")

    // Aircraft list (MutableState for Compose awareness)
    var aircraftList: SnapshotStateList<Aircraft> = mutableStateListOf()
    private var _defaultAircraft: Aircraft? by mutableStateOf(null)
    val defaultAircraft: Aircraft? get() = _defaultAircraft

    private val AIRCRAFT_LIST_KEY = stringPreferencesKey("aircraft_list")
    private val DEFAULT_AIRCRAFT_KEY = stringPreferencesKey("default_aircraft")
    private val DECIMAL_PRECISION_KEY = intPreferencesKey("decimal_precision")

    // Call this on init
    suspend fun loadAircraftPrefs() {
        val prefs = context.dataStore.data.first()
        val aircraftJson = prefs[AIRCRAFT_LIST_KEY] ?: "[]"
        val loadedAircraft = Json.decodeFromString<List<Aircraft>>(aircraftJson)
        aircraftList.clear()
        aircraftList.addAll(loadedAircraft)
        val defaultName = prefs[DEFAULT_AIRCRAFT_KEY]
        _defaultAircraft = aircraftList.find { it.name == defaultName }

        // Load decimal precision
        decimalPrecision = prefs[DECIMAL_PRECISION_KEY] ?: 2
    }

    suspend fun saveAircraftPrefs() {
        context.dataStore.edit { prefs ->
            prefs[AIRCRAFT_LIST_KEY] = Json.encodeToString(aircraftList.toList())
            prefs[DEFAULT_AIRCRAFT_KEY] = _defaultAircraft?.name ?: ""
            prefs[DECIMAL_PRECISION_KEY] = decimalPrecision
        }
    }

    fun addAircraft(aircraft: Aircraft) {
        aircraftList.add(aircraft)
        CoroutineScope(Dispatchers.IO).launch { saveAircraftPrefs() }
    }

    fun deleteAircraft(aircraft: Aircraft) {
        aircraftList.remove(aircraft)
        if (_defaultAircraft == aircraft) _defaultAircraft = aircraftList.firstOrNull()
        CoroutineScope(Dispatchers.IO).launch { saveAircraftPrefs() }
    }

    fun editAircraft(oldAircraft: Aircraft, newAircraft: Aircraft) {
        val idx = aircraftList.indexOf(oldAircraft)
        if (idx >= 0) aircraftList[idx] = newAircraft
        if (_defaultAircraft == oldAircraft) _defaultAircraft = newAircraft
        CoroutineScope(Dispatchers.IO).launch { saveAircraftPrefs() }
    }

    fun setDefaultAircraft(aircraft: Aircraft) {
        _defaultAircraft = aircraft
        CoroutineScope(Dispatchers.IO).launch { saveAircraftPrefs() }
    }

    fun updateLanguage(newLanguage: AppLanguage) {
        language = newLanguage
        CoroutineScope(Dispatchers.IO).launch {
            languagePreference.setLanguage(newLanguage)
        }
    }

    fun updateDecimalPrecision(precision: Int) {
        decimalPrecision = precision.coerceIn(0, 8) // Limit to 0-8 decimal places
        CoroutineScope(Dispatchers.IO).launch { saveAircraftPrefs() }
    }
}
