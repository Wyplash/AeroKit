package com.alex.aerokit.util

import androidx.compose.runtime.Composable
import com.alex.aerokit.ui.theme.AppLanguage
import com.alex.aerokit.ui.theme.LocalAppLanguage

object Strings {
    // ---- General/Settings ----
    @Composable
    fun settings() = settings(LocalAppLanguage.current)
    fun settings(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.FRENCH -> "Paramètres"
    }

    @Composable
    fun theme() = theme(LocalAppLanguage.current)
    fun theme(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Theme"
        AppLanguage.FRENCH -> "Thème"
    }

    @Composable
    fun language() = language(LocalAppLanguage.current)
    fun language(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.FRENCH -> "Langue"
    }

    @Composable
    fun aboutHelp() = aboutHelp(LocalAppLanguage.current)
    fun aboutHelp(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "About / Help"
        AppLanguage.FRENCH -> "À propos / Aide"
    }

    @Composable
    fun version(versionName: String) = version(versionName, LocalAppLanguage.current)
    fun version(versionName: String, language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Version: $versionName"
        AppLanguage.FRENCH -> "Version : $versionName"
    }

    @Composable
    fun contactSupport() = contactSupport(LocalAppLanguage.current)
    fun contactSupport(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Contact / Support / Feedback"
        AppLanguage.FRENCH -> "Contact / Support / Retour"
    }

    @Composable
    fun privacyPolicy() = privacyPolicy(LocalAppLanguage.current)
    fun privacyPolicy(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Privacy Policy"
        AppLanguage.FRENCH -> "Politique de confidentialité"
    }

    @Composable
    fun termsOfService() = termsOfService(LocalAppLanguage.current)
    fun termsOfService(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Terms of Service"
        AppLanguage.FRENCH -> "Conditions d'utilisation"
    }

    @Composable
    fun light() = light(LocalAppLanguage.current)
    fun light(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Light"
        AppLanguage.FRENCH -> "Clair"
    }

    @Composable
    fun dark() = dark(LocalAppLanguage.current)
    fun dark(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Dark"
        AppLanguage.FRENCH -> "Sombre"
    }

    @Composable
    fun auto() = auto(LocalAppLanguage.current)
    fun auto(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Auto"
        AppLanguage.FRENCH -> "Auto"
    }

    @Composable
    fun english() = english(LocalAppLanguage.current)
    fun english(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "English"
        AppLanguage.FRENCH -> "Anglais"
    }

    @Composable
    fun french() = french(LocalAppLanguage.current)
    fun french(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "French"
        AppLanguage.FRENCH -> "Français"
    }

    // --- Conversion screen ---
    @Composable
    fun conversionComingSoon() = conversionComingSoon(LocalAppLanguage.current)
    fun conversionComingSoon(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Conversion Coming Soon"
        AppLanguage.FRENCH -> "Conversion à venir"
    }

    // --- Wind Component screen ---
    @Composable
    fun windComponentTab() = windComponentTab(LocalAppLanguage.current)
    fun windComponentTab(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Wind Component"
        AppLanguage.FRENCH -> "Composante vent"
    }

    @Composable
    fun runwayLabel() = runwayLabel(LocalAppLanguage.current)
    fun runwayLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Runway (09, 26, or 264°)"
        AppLanguage.FRENCH -> "Piste (09, 26 ou 264°)"
    }

    @Composable
    fun windDirectionLabel() = windDirectionLabel(LocalAppLanguage.current)
    fun windDirectionLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Wind Direction (°)"
        AppLanguage.FRENCH -> "Direction du vent (°)"
    }

    @Composable
    fun windSpeedLabel() = windSpeedLabel(LocalAppLanguage.current)
    fun windSpeedLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Wind Speed (kt)"
        AppLanguage.FRENCH -> "Vitesse du vent (kt)"
    }

    @Composable
    fun crosswindLimitLabel() = crosswindLimitLabel(LocalAppLanguage.current)
    fun crosswindLimitLabel(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Crosswind Limit (kt)"
        AppLanguage.FRENCH -> "Limite vent traversier (kt)"
    }

    @Composable
    fun crosswindResult(value: String) = crosswindResult(value, LocalAppLanguage.current)
    fun crosswindResult(value: String, language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Crosswind: $value"
        AppLanguage.FRENCH -> "Vent traversier : $value"
    }

    fun headwind(value: Int, language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Headwind: $value kt"
        AppLanguage.FRENCH -> "Vent de face : $value kt"
    }

    fun tailwind(value: Int, language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> "Tailwind: $value kt"
        AppLanguage.FRENCH -> "Vent arrière : $value kt"
    }
@Composable
    fun headwind(value: Int): String = when (LocalAppLanguage.current) {
        AppLanguage.ENGLISH -> "Headwind: $value kt"
        AppLanguage.FRENCH -> "Vent de face : $value kt"
    }

    @Composable
    fun tailwind(value: Int): String = when (LocalAppLanguage.current) {
        AppLanguage.ENGLISH -> "Tailwind: $value kt"
        AppLanguage.FRENCH -> "Vent arrière : $value kt"
    }

    @Composable
    fun left(): String = when (LocalAppLanguage.current) {
        AppLanguage.ENGLISH -> "Left"
        AppLanguage.FRENCH -> "Gauche"
    }

    @Composable
    fun right(): String = when (LocalAppLanguage.current) {
        AppLanguage.ENGLISH -> "Right"
        AppLanguage.FRENCH -> "Droite"
    }
    fun windComponent(language: AppLanguage): String =
        when (language) {
            AppLanguage.ENGLISH -> "Wind Component"
            AppLanguage.FRENCH -> "Composante vent"
    }
}
