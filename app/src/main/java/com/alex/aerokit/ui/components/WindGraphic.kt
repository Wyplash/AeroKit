package com.alex.aerokit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * Draws a stylized runway and wind arrow graphic.
 * - The runway is always visible.
 * - The arrow color turns red if crosswind limit is exceeded.
 * - The arrow points **toward the center** from the wind direction.
 */
@Composable
fun WindGraphic(
    runwayHeading: Int,
    windDir: Int,
    windSpeed: Int,
    crossLimitExceeded: Boolean,
    showArrow: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    // Calculate wind/runway angle (degrees)
    val delta = ((windDir - runwayHeading + 360) % 360).toFloat()
    val angleRad = Math.toRadians(delta.toDouble())

    Box(
        modifier = modifier.size(size),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.toPx()
            val h = w // Always square for a symmetric runway
            val cx = w / 2
            val cy = h / 2

            // Draw runway body (dark gray, with rounded thresholds)
            val runwayWidth = w * 0.25f
            val runwayLength = w * 0.70f
            val thresholdRadius = runwayWidth * 0.36f
            val runwayColor = Color(0xFF23252A)

            drawRoundRect(
                color = runwayColor,
                topLeft = Offset(cx - runwayWidth / 2, cy - runwayLength / 2),
                size = Size(runwayWidth, runwayLength),
                cornerRadius = CornerRadius(thresholdRadius, thresholdRadius)
            )

            // Draw dashed centerline
            val lineStep = runwayLength / 10f
            val dashLength = lineStep * 0.6f
            val dashWidth = runwayWidth * 0.13f
            for (i in 0..9 step 2) {
                val yStart = cy - runwayLength / 2 + i * lineStep
                drawLine(
                    color = Color.White,
                    start = Offset(cx, yStart),
                    end = Offset(cx, yStart + dashLength),
                    strokeWidth = dashWidth,
                    cap = StrokeCap.Round
                )
            }

            // Draw wind arrow if fields are valid and wind speed > 0
            if (showArrow && windSpeed > 0) {
                val arrowLen = w * 0.36f
                val shaftWidth = w * 0.022f
                val arrowColor = if (crossLimitExceeded) Color.Red else Color(0xFF2196F3)

                // Arrow points **toward the center** from wind direction
                val arrowStart = Offset(
                    cx + arrowLen * sin(angleRad).toFloat(),
                    cy - arrowLen * cos(angleRad).toFloat()
                )
                val arrowEnd = Offset(cx, cy)

                // Draw shaft
                drawLine(
                    color = arrowColor,
                    start = arrowStart,
                    end = arrowEnd,
                    strokeWidth = shaftWidth,
                    cap = StrokeCap.Round
                )

                // Draw arrowhead
                val headLength = w * 0.08f
                val headAngle = Math.PI / 8
                val dir = atan2(arrowEnd.y - arrowStart.y, arrowEnd.x - arrowStart.x)
                val leftWing = Offset(
                    arrowEnd.x - headLength * cos(dir - headAngle).toFloat(),
                    arrowEnd.y - headLength * sin(dir - headAngle).toFloat()
                )
                val rightWing = Offset(
                    arrowEnd.x - headLength * cos(dir + headAngle).toFloat(),
                    arrowEnd.y - headLength * sin(dir + headAngle).toFloat()
                )
                drawLine(
                    color = arrowColor,
                    start = arrowEnd,
                    end = leftWing,
                    strokeWidth = shaftWidth * 0.8f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = arrowColor,
                    start = arrowEnd,
                    end = rightWing,
                    strokeWidth = shaftWidth * 0.8f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
