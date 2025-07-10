package com.alex.aerokit.util

// Validates runway input: 2 digits (1–36) or 3 digits (0–359)
fun validateRunway(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    input.length > 3 -> "Too many digits"
    input.length == 1 -> {
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

// Validates wind direction: 0–359
fun validateWindDir(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> {
        val n = input.toIntOrNull() ?: return "Invalid"
        if (n in 0..359) null else "Heading must be 0–359"
    }
}

// Validates wind speed: must be a number (positive integer)
fun validateWindSpeed(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> null
}

// Validates crosswind limit: must be a number (positive integer)
fun validateCrossLimit(input: String): String? = when {
    input.isEmpty() -> null
    input.any { !it.isDigit() } -> "Numbers only"
    else -> null
}
