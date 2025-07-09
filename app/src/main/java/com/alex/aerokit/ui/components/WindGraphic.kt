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

@Composable
fun WindGraphic(
    runwayHeading: Int,
    windDir: Int,
    windSpeed: Int,
    crossLimit: Int = 0,
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
            val h = w // square canvas
            val cx = w / 2
            val cy = h / 2

            // ---- Draw runway as rounded rectangle ----
            val runwayWidth = w * 0.20f
            val runwayLength = w * 0.85f
            val thresholdRadius = runwayWidth * 0.36f
            val runwayColor = Color(0xFF23252A)

            // Runway main body (darker)
            drawRoundRect(
                color = runwayColor,
                topLeft = Offset(cx - runwayWidth / 2, cy - runwayLength / 2),
                size = Size(runwayWidth, runwayLength),
                cornerRadius = CornerRadius(thresholdRadius, thresholdRadius)
            )

            // Centerline (white, dashed)
            val lineStep = runwayLength / 12f
            val dashLength = lineStep * 0.6f
            val dashWidth = runwayWidth * 0.10f
            for (i in 0..10 step 2) {
                val yStart = cy - runwayLength / 2 + i * lineStep
                drawLine(
                    color = Color.White,
                    start = Offset(cx, yStart),
                    end = Offset(cx, yStart + dashLength),
                    strokeWidth = dashWidth,
                    cap = StrokeCap.Round
                )
            }

            // ---- Draw wind arrow (if valid) ----
            if (showArrow && windSpeed > 0) {
                val arrowLen = w * 0.38f
                val shaftWidth = w * 0.022f
                val arrowColor = Color(0xFF2196F3)

                // The arrow points **toward the center**
                val arrowStart = Offset(
                    cx + arrowLen * sin(angleRad).toFloat(),
                    cy - arrowLen * cos(angleRad).toFloat()
                )
                val arrowEnd = Offset(cx, cy)

                // Arrow shaft
                drawLine(
                    color = arrowColor,
                    start = arrowStart,
                    end = arrowEnd,
                    strokeWidth = shaftWidth,
                    cap = StrokeCap.Round
                )

                // Arrowhead
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
