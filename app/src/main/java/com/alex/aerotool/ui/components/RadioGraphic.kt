package com.alex.aerotool.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun RadioGraphic(
    aircraftAltitude: Double, // in feet
    radioStationAltitude: Double, // in feet
    horizontalDistance: Double, // in km
    showGraphic: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val textMeasurer = rememberTextMeasurer()

    if (!showGraphic) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Convert altitudes to meters for calculation
        val aircraftAltM = aircraftAltitude * 0.3048
        val radioAltM = radioStationAltitude * 0.3048
        val horizontalDistanceM = horizontalDistance * 1000

        // Calculate scale factors - ensure both objects are always visible
        val maxAltitude = maxOf(aircraftAltM, radioAltM, 50.0) + 50 // Much lower minimum scale
        val altitudeScale = (canvasHeight * 0.45) / maxAltitude // Use 45% of canvas height

        // For distance, use a reasonable scale that doesn't compress too much
        val maxDisplayDistance =
            maxOf(horizontalDistanceM.toFloat(), 5000f) // Minimum 5km for scale
        val distanceScale = (canvasWidth * 0.5f) / maxDisplayDistance // Use 50% of canvas width

        // -- Calculate true slant (LOS) and ground (horizontal) distance --
        val deltaHeightFt = abs(aircraftAltitude - radioStationAltitude)
        val slantDistanceNm = horizontalDistance.toFloat() // LOS (NM)
        val deltaHeightNm = (deltaHeightFt / 6076.12).toFloat() // Convert ft to NM
        val groundDistanceNm = if (slantDistanceNm > deltaHeightNm) {
            sqrt((slantDistanceNm * slantDistanceNm) - (deltaHeightNm * deltaHeightNm))
        } else 0.1f // never zero
        // Use the full width for max possible ground distance
        val fullWidthPx = size.width * 0.85f // Float
        val pxPerNm = fullWidthPx / groundDistanceNm.coerceAtLeast(1e-2f)
        val leftMargin = size.width * 0.075f
        val radioX = leftMargin
        val aircraftX = radioX + (groundDistanceNm * pxPerNm)
        val groundY = canvasHeight * 0.7f
        val radioY = groundY - (radioAltM * altitudeScale).toFloat()
        val aircraftY = groundY - (aircraftAltM * altitudeScale).toFloat()

        // Draw sky gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF87CEEB), // Sky blue
                    Color(0xFFE0F6FF)  // Light blue
                ),
                startY = 0f,
                endY = groundY
            ),
            topLeft = Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(canvasWidth, groundY)
        )

        // Draw ground
        drawRect(
            color = Color(0xFF8FBC8F), // Dark sea green
            topLeft = Offset(0f, groundY),
            size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight - groundY)
        )

        // Draw ground line
        drawLine(
            color = Color(0xFF228B22), // Forest green
            start = Offset(0f, groundY),
            end = Offset(canvasWidth, groundY),
            strokeWidth = 3.dp.toPx()
        )

        // Draw radio tower with better design
        drawEnhancedRadioTower(radioX, radioY, groundY, primaryColor, secondaryColor)

        // Draw enhanced aircraft
        drawEnhancedAircraft(aircraftX, aircraftY, primaryColor, secondaryColor)

        // Draw line-of-sight connection with animation-like effect
        drawEnhancedLineOfSight(radioX, radioY, aircraftX, aircraftY, primaryColor)

        // Draw horizontal ground distance line at the base
        val groundLineY = groundY + 16.dp.toPx()
        drawLine(
            color = Color.DarkGray,
            start = Offset(radioX, groundLineY),
            end = Offset(aircraftX, groundLineY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 5f))
        )
        // Add small vertical ticks at ends
        drawLine(
            color = Color.DarkGray,
            start = Offset(radioX, groundLineY - 8.dp.toPx()),
            end = Offset(radioX, groundLineY + 8.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.DarkGray,
            start = Offset(aircraftX, groundLineY - 8.dp.toPx()),
            end = Offset(aircraftX, groundLineY + 8.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        // Draw ground distance text in the middle
        val groundText = String.format("%.1f NM", groundDistanceNm)
        val groundTextResult = textMeasurer.measure(
            text = groundText,
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )
        val groundTextX = (radioX + aircraftX) / 2f - groundTextResult.size.width / 2f
        val groundTextY = groundLineY + 8.dp.toPx()
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(groundTextX - 4.dp.toPx(), groundTextY - 2.dp.toPx()),
            size = Size(
                groundTextResult.size.width + 8.dp.toPx(),
                groundTextResult.size.height + 4.dp.toPx()
            ),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
        drawText(
            textMeasurer = textMeasurer,
            text = groundText,
            topLeft = Offset(groundTextX, groundTextY),
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )

        // Draw altitude labels
        drawAltitudeLabelAtTop(
            x = radioX,
            y = radioY,
            altitude = radioStationAltitude,
            label = "Tower",
            color = Color.Black,
            textMeasurer = textMeasurer
        )

        drawAltitudeLabelAtTop(
            x = aircraftX,
            y = aircraftY,
            altitude = aircraftAltitude,
            label = "Aircraft",
            color = Color.Black,
            textMeasurer = textMeasurer
        )

        // Draw Earth's curvature hint (subtle arc)
        drawEarthCurvature(canvasWidth, groundY, surfaceVariantColor)
    }
}

private fun DrawScope.drawEnhancedRadioTower(
    x: Float, y: Float, groundY: Float,
    primaryColor: Color, secondaryColor: Color
) {
    val towerWidth = 6.dp.toPx()
    val towerHeight = groundY - y

    // Draw tower base (wider at bottom)
    drawLine(
        color = primaryColor,
        start = Offset(x - towerWidth / 4, groundY),
        end = Offset(x + towerWidth / 4, groundY),
        strokeWidth = 8.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Draw tower mast
    drawLine(
        color = primaryColor,
        start = Offset(x, groundY),
        end = Offset(x, y),
        strokeWidth = towerWidth,
        cap = StrokeCap.Round
    )

    // Draw support struts
    val struts = 3
    for (i in 1..struts) {
        val strutY = groundY - (towerHeight * i / (struts + 1))
        val strutWidth = 15.dp.toPx() * (1 - i * 0.2f)
        drawLine(
            color = primaryColor.copy(alpha = 0.7f),
            start = Offset(x - strutWidth / 2, strutY),
            end = Offset(x + strutWidth / 2, strutY),
            strokeWidth = 2.dp.toPx()
        )
    }

    // Draw main antenna array
    val antennaWidth = 25.dp.toPx()
    drawLine(
        color = secondaryColor,
        start = Offset(x - antennaWidth / 2, y),
        end = Offset(x + antennaWidth / 2, y),
        strokeWidth = 4.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Draw secondary antennas
    for (i in 1..2) {
        val antY = y + i * 8.dp.toPx()
        val antWidth = antennaWidth * (1 - i * 0.3f)
        drawLine(
            color = secondaryColor,
            start = Offset(x - antWidth / 2, antY),
            end = Offset(x + antWidth / 2, antY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Draw radio waves (enhanced)
    for (i in 1..4) {
        val radius = i * 18.dp.toPx()
        val alpha = 1f - (i * 0.2f)
        drawArc(
            color = secondaryColor.copy(alpha = alpha),
            startAngle = -60f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(x - radius, y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawEnhancedAircraft(
    x: Float, y: Float,
    primaryColor: Color, secondaryColor: Color
) {
    val aircraftLength = 24.dp.toPx()
    val aircraftHeight = 8.dp.toPx()

    // Draw aircraft fuselage (main body)
    val fuselage = Path().apply {
        moveTo(x - aircraftLength / 2, y)
        lineTo(x + aircraftLength / 3, y - aircraftHeight / 4)
        lineTo(x + aircraftLength / 2, y - aircraftHeight / 6)
        lineTo(x + aircraftLength / 2, y + aircraftHeight / 6)
        lineTo(x + aircraftLength / 3, y + aircraftHeight / 4)
        close()
    }

    drawPath(
        path = fuselage,
        color = primaryColor
    )

    // Draw main wings
    drawLine(
        color = secondaryColor,
        start = Offset(x - aircraftLength / 6, y - aircraftHeight / 2),
        end = Offset(x - aircraftLength / 6, y + aircraftHeight / 2),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Draw tail
    drawLine(
        color = secondaryColor,
        start = Offset(x - aircraftLength / 2, y - aircraftHeight / 3),
        end = Offset(x - aircraftLength / 2, y + aircraftHeight / 6),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Draw cockpit window
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = 2.dp.toPx(),
        center = Offset(x + aircraftLength / 4, y)
    )
}

private fun DrawScope.drawEnhancedLineOfSight(
    startX: Float, startY: Float,
    endX: Float, endY: Float,
    color: Color
) {
    // Draw main line-of-sight line
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 3.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 8f))
    )

    // Draw signal strength indicators along the line
    val steps = 5
    for (i in 1 until steps) {
        val t = i.toFloat() / steps
        val signalX = startX + t * (endX - startX)
        val signalY = startY + t * (endY - startY)
        val signalSize = 3.dp.toPx() * (1 - t * 0.3f)

        drawCircle(
            color = color.copy(alpha = 0.6f),
            radius = signalSize,
            center = Offset(signalX, signalY)
        )
    }
}

private fun DrawScope.drawAltitudeLabelAtTop(
    x: Float,
    y: Float,
    altitude: Double,
    label: String,
    color: Color,
    textMeasurer: TextMeasurer
) {
    val altText = if (altitude < 1000) {
        String.format("%.0f ft", altitude)
    } else String.format("%.1fk ft", altitude / 1000.0)
    val labelText = "$label\n$altText"
    val textResult = textMeasurer.measure(
        text = labelText,
        style = TextStyle(fontSize = 12.sp, color = color)
    )
    val textX = x - textResult.size.width / 2
    val textY = y - textResult.size.height - 10.dp.toPx()
    // Draw background
    drawRoundRect(
        color = Color.White.copy(alpha = 0.95f),
        topLeft = Offset(textX - 4.dp.toPx(), textY - 2.dp.toPx()),
        size = Size(textResult.size.width + 8.dp.toPx(), textResult.size.height + 4.dp.toPx()),
        cornerRadius = CornerRadius(6.dp.toPx())
    )
    // Draw border
    drawRoundRect(
        color = color.copy(alpha = 0.8f),
        topLeft = Offset(textX - 4.dp.toPx(), textY - 2.dp.toPx()),
        size = Size(textResult.size.width + 8.dp.toPx(), textResult.size.height + 4.dp.toPx()),
        cornerRadius = CornerRadius(6.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
    // Text
    drawText(
        textMeasurer = textMeasurer,
        text = labelText,
        topLeft = Offset(textX, textY),
        style = TextStyle(fontSize = 12.sp, color = color)
    )
}

private fun DrawScope.drawEarthCurvature(
    canvasWidth: Float, groundY: Float,
    color: Color
) {
    // Draw a subtle curve to hint at Earth's curvature
    val path = Path().apply {
        moveTo(0f, groundY)
        quadraticBezierTo(
            canvasWidth / 2, groundY + 10.dp.toPx(),
            canvasWidth, groundY
        )
    }

    drawPath(
        path = path,
        color = color.copy(alpha = 0.3f),
        style = Stroke(width = 1.dp.toPx())
    )
}