package com.alex.aerokit.util

/**
 * Validates runway input.
 * - 2 digits: Must be between 1–36 (runway number)
 * - 3 digits: Must be between 0–359 (true heading)
 * - Returns error string or null if valid (or still typing)
 */
fun validateRunway(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    input.length > 3 -> "Too many digits"
    input.length == 1 -> { // single digit, must be 1-9 (realistic runways)
        val n = input.toIntOrNull() ?: return "Invalid"
        if (n in 1..9) null else "Runway must be 1–36"
    }
    input.length <= 2 -> {
        val n = input.toIntOrNull() ?: return "Invalid"
        if (n in 1..36) null else "Runway must be 1–36"
    }
    input.length == 3 -> {
        val n = input.toIntOrNull() ?: return "Invalid"
        if (n in 0..359) null else "Heading must be 000–359"
    }
    else -> "Invalid"
}

fun validateWindDir(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    input.length > 3 -> "Too many digits"
    else -> {
        val n = input.toIntOrNull() ?: return "Invalid"
        if (n in 0..359) null else "Heading must be 0–359"
    }
}
fun validateWindSpeed(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> null
}

fun validateCrossLimit(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> null
}

