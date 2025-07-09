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
    windDir: Int,
    windSpeed: Int,
    runwayHeading: Int,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    // Calculate relative wind angle
    val delta = ((windDir - runwayHeading + 360) % 360).toFloat()
    val angleRad = Math.toRadians(delta.toDouble())

    // Colors (direct Compose Color values)
    val runwayColor = Color.Gray
    val windArrowColor = Color(0xFF00BCD4) // Cyan-ish
    val arrowStroke = 0.05f // Relative thickness

    Box(
        modifier = modifier.size(size).padding(8.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.toPx()
            val cx = w / 2
            val cy = w / 2
            val radius = w * 0.35f

            // Runway (vertical)
            drawLine(
                color = runwayColor,
                start = Offset(cx, cy - radius),
                end = Offset(cx, cy + radius),
                strokeWidth = w * 0.10f,
                cap = StrokeCap.Round
            )

            // Wind arrow
            val windLen = radius * 1.1f
            val wx = cx + (windLen * sin(angleRad)).toFloat()
            val wy = cy - (windLen * cos(angleRad)).toFloat()

            drawLine(
                color = windArrowColor,
                start = Offset(cx, cy),
                end = Offset(wx, wy),
                strokeWidth = w * arrowStroke,
                cap = StrokeCap.Round
            )

            // Arrowhead
            val arrowHead = w * 0.08f
            val angle = atan2(wx - cx, cy - wy)
            val leftWing = Offset(
                wx - arrowHead * cos(angle - 0.5f),
                wy + arrowHead * sin(angle - 0.5f)
            )
            val rightWing = Offset(
                wx - arrowHead * cos(angle + 0.5f),
                wy + arrowHead * sin(angle + 0.5f)
            )
            drawLine(windArrowColor, Offset(wx, wy), leftWing, strokeWidth = w * 0.03f, cap = StrokeCap.Round)
            drawLine(windArrowColor, Offset(wx, wy), rightWing, strokeWidth = w * 0.03f, cap = StrokeCap.Round)
        }
    }
}
