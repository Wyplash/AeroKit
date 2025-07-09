package com.alex.aerokit.util

/** Converts runway input to heading in degrees.
 *  - 2 digits (e.g., "26") → 260
 *  - 3 digits (e.g., "264") → 264
 */
fun parseRunwayInput(input: String): Int {
    val n = input.toIntOrNull() ?: return 0
    return if (input.length <= 2) (n * 10) % 360 else n % 360
}
