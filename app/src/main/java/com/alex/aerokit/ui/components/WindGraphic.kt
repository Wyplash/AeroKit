package com.alex.aerokit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun WindGraphic(
    runwayHeading: Int,
    windDir: Int,
    windSpeed: Int,
    crossLimit: Int?,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    showArrow: Boolean = true
) {
    val cyan = Color(0xFF00BCD4)

    // Arrow color logic
    val delta = ((windDir - runwayHeading + 360) % 360).toFloat()
    val angleRad = Math.toRadians(delta.toDouble())
    val crosswind = windSpeed * sin(angleRad)
    val arrowOverLimit = crossLimit != null && crossLimit > 0 && abs(crosswind) > crossLimit
    val arrowColor = if (arrowOverLimit) Color.Red else cyan

    Box(
        modifier = modifier.size(size).padding(8.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.toPx()
            val cx = w / 2
            val cy = w / 2
            val radius = w * 0.33f

            // Runway (vertical line)
            drawLine(
                color = Color.Gray,
                start = Offset(cx, cy - radius),
                end = Offset(cx, cy + radius),
                strokeWidth = w * 0.09f,
                cap = StrokeCap.Round
            )

            if (
                showArrow &&
                windSpeed > 0 &&
                (runwayHeading in 0..359) &&
                (windDir in 0..359)
            ) {
                // Calculate FROM which direction wind comes: arrow points **to** center
                val windAngle = Math.toRadians((windDir - runwayHeading).toDouble())
                val tailX = cx + radius * sin(windAngle).toFloat()
                val tailY = cy - radius * cos(windAngle).toFloat()
                val headX = cx
                val headY = cy

                // Main shaft
                drawLine(
                    color = arrowColor,
                    start = Offset(tailX, tailY),
                    end = Offset(headX, headY),
                    strokeWidth = w * 0.05f,
                    cap = StrokeCap.Round
                )

                // Arrowhead (always at center)
                val arrowHead = w * 0.07f
                val shaftAngle = atan2(headY - tailY, headX - tailX)
                val leftWing = Offset(
                    (headX - arrowHead * cos(shaftAngle - 0.5f)).toFloat(),
                    (headY - arrowHead * sin(shaftAngle - 0.5f)).toFloat()
                )
                val rightWing = Offset(
                    (headX - arrowHead * cos(shaftAngle + 0.5f)).toFloat(),
                    (headY - arrowHead * sin(shaftAngle + 0.5f)).toFloat()
                )
                drawLine(arrowColor, Offset(headX, headY), leftWing, strokeWidth = w * 0.025f, cap = StrokeCap.Round)
                drawLine(arrowColor, Offset(headX, headY), rightWing, strokeWidth = w * 0.025f, cap = StrokeCap.Round)
            }
        }
    }
}

