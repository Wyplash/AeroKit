package com.alex.aerokit.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alex.aerokit.ui.theme.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")
private val LANGUAGE_KEY = stringPreferencesKey("language_code")

class LanguagePreference(private val context: Context) {
    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        when (prefs[LANGUAGE_KEY] ?: "en") {  // fallback to en
            "fr" -> AppLanguage.FRENCH
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.ENGLISH
        }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = when (lang) {
                AppLanguage.FRENCH -> "fr"
                AppLanguage.ENGLISH -> "en"
            }
        }
    }
}
