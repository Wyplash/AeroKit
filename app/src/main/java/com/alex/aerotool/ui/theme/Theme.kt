package com.alex.aerotool.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AviationColorScheme = darkColorScheme(
    primary = AviationGold,              // Dark gold for primary actions
    onPrimary = AviationNavy,            // Navy text on gold backgrounds
    secondary = AviationAccentGold,      // Lighter gold for secondary elements
    onSecondary = AviationNavy,          // Navy text on lighter gold
    tertiary = AviationLightNavy,        // Light navy for tertiary elements
    onTertiary = AviationWhite,          // White text on navy
    background = AviationNavy,           // Deep navy background
    onBackground = AviationWhite,        // White text on navy background
    surface = AviationLightNavy,         // Light navy for cards/surfaces
    onSurface = AviationWhite,           // White text on surfaces
    surfaceVariant = AviationDarkGrey,   // Dark grey for variants
    onSurfaceVariant = AviationLightGrey, // Light grey text
    outline = AviationGrey,              // Grey for outlines
    error = Color(0xFFCF6679),           // Standard error color
    onError = AviationNavy               // Navy text on error
)

@Composable
fun AeroKitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AviationColorScheme,
        typography = Typography,
        content = content
    )
}
