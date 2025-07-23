package com.alex.aerotool.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alex.aerotool.ui.theme.ThemeController
import kotlin.math.round

// Helper functions for time calculations
private fun timeToMinutes(time: String): Int {
    val parts = time.split(":")
    if (parts.size != 2) return 0
    val hours = parts[0].toIntOrNull() ?: 0
    val minutes = parts[1].toIntOrNull() ?: 0
    return hours * 60 + minutes
}

private fun minutesToTime(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format("%d:%02d", hours, minutes)
}

private fun evaluateTimeExpression(expr: String): String {
    try {
        // Pre-process: build canonical time expression (e.g., pad 15 to 0015 for 00:15)
        val preprocessed = expr.replace(" ", "")

        // Split into time blocks and operators
        val parts = mutableListOf<String>()
        var curr = ""
        for (c in preprocessed) {
            if (c == '+' || c == '-') {
                if (curr.isNotEmpty()) parts.add(curr)
                parts.add(c.toString())
                curr = ""
            } else {
                curr += c
            }
        }
        if (curr.isNotEmpty()) parts.add(curr)

        // Map time blocks: interpret as up to 4 digits, format as HH:MM
        val canonical = parts.map {
            if (it == "+" || it == "-") it else formatTimeInput(it)
        }

        // Calculate
        var totalMinutes = 0
        var op = "+"
        for (item in canonical) {
            when (item) {
                "+", "-" -> op = item
                else -> {
                    val min = timeToMinutes(item)
                    if (op == "+") totalMinutes += min else totalMinutes -= min
                }
            }
        }
        return minutesToTime(totalMinutes)
    } catch (e: Exception) {
        return "Error"
    }
}

enum class CalculatorScreenState {
    HIDDEN,
    CALCULATOR
}

enum class CalculatorMode {
    NORMAL,
    TIME
}

// Helper for time calculator input
fun formatTimeInput(raw: String): String {
    // Remove colons and non-digit characters
    val digits = raw.filter { it.isDigit() }
    // Left pad with zeros up to 4 digits
    val padded = digits.padStart(4, '0').takeLast(4)
    val hours = padded.substring(0, 2)
    val minutes = padded.substring(2, 4)
    return "$hours:$minutes"
}

@Composable
fun CalculatorFabAndSheet(
    expression: String,
    result: String,
    setCalculatorState: (String, String) -> Unit,
    onTimeConversionClick: () -> Unit,
    themeController: ThemeController
) {
    var screenState by remember { mutableStateOf(CalculatorScreenState.HIDDEN) }
    var calculatorMode by remember { mutableStateOf(CalculatorMode.NORMAL) }

    Box(Modifier.fillMaxSize()) {
        when (screenState) {
            CalculatorScreenState.CALCULATOR -> {
                Surface(
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(Modifier.fillMaxSize()) {
                        AeroTopBar(
                            title = "AeroTool",
                            onBackClick = {
                                screenState = CalculatorScreenState.HIDDEN
                                calculatorMode =
                                    CalculatorMode.NORMAL // Reset to normal mode when closing
                            }
                        )
                        CalculatorContent(
                            expression = expression,
                            result = result,
                            setCalculatorState = setCalculatorState,
                            onTimeConversionClick = {
                                screenState = CalculatorScreenState.HIDDEN
                                onTimeConversionClick()
                            },
                            onCloseCalculator = {
                                screenState = CalculatorScreenState.HIDDEN
                                calculatorMode = CalculatorMode.NORMAL
                            },
                            calculatorMode = calculatorMode,
                            onModeChange = { calculatorMode = it }
                        )
                    }
                }
            }

            CalculatorScreenState.HIDDEN -> {
                FloatingActionButton(
                    onClick = {
                        screenState = CalculatorScreenState.CALCULATOR
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Outlined.Calculate, contentDescription = "Calculator")
                }
            }
        }
    }
}

@Composable
private fun CalculatorContent(
    expression: String,
    result: String,
    setCalculatorState: (String, String) -> Unit,
    onTimeConversionClick: () -> Unit,
    onCloseCalculator: () -> Unit,
    calculatorMode: CalculatorMode,
    onModeChange: (CalculatorMode) -> Unit
) {
    fun eval(expr: String): String {
        try {
            val input = expr.replace('×', '*').replace('÷', '/')
            val opRegex = Regex("([+\\-*/])")
            val tokens = opRegex.split(input).filter { it.isNotBlank() }
            val ops = opRegex.findAll(input).map { it.value }.toList()
            if (tokens.isEmpty()) return ""
            var acc = tokens[0].toDoubleOrNull() ?: return "Error"
            for (i in ops.indices) {
                val next = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: return "Error"
                when (ops[i]) {
                    "+" -> acc += next
                    "-" -> acc -= next
                    "*" -> acc *= next
                    "/" -> acc /= next
                }
            }
            val rounded = round(acc * 1e8) / 1e8
            return if ((rounded % 1) == 0.0) rounded.toLong().toString() else rounded.toString()
        } catch (e: Exception) {
            return "Error"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (calculatorMode == CalculatorMode.TIME) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "HH:MM",
                modifier = Modifier.align(Alignment.End),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Card(
            shape = RoundedCornerShape(if (calculatorMode == CalculatorMode.TIME) 30.dp else 22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 9.dp),
            colors = CardDefaults.cardColors(containerColor = if (calculatorMode == CalculatorMode.TIME) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface),
            border = if (calculatorMode == CalculatorMode.TIME) BorderStroke(
                2.dp,
                Color(0xFF4CAF50)
            ) else null
        ) {
            Column(
                Modifier.padding(horizontal = 22.dp, vertical = 14.dp)
            ) {
                val displayExpression = when {
                    calculatorMode == CalculatorMode.TIME -> {
                        // Split by + and -
                        val sb = StringBuilder()
                        var buffer = ""
                        for (c in expression) {
                            if (c == '+' || c == '-') {
                                // Append formatted block then operator
                                sb.append(formatTimeInput(buffer))
                                sb.append(c)
                                buffer = ""
                            } else {
                                buffer += c
                            }
                        }
                        // Format last block
                        sb.append(
                            if (buffer.isEmpty() && (expression.endsWith("+") || expression.endsWith(
                                    "-"
                                ))
                            )
                                "00:00"
                            else
                                formatTimeInput(buffer)
                        )
                        sb.toString()
                    }
                    else -> expression.ifEmpty { "0" }
                }
                Text(
                    text = displayExpression,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                if (result.isNotEmpty()) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = if (calculatorMode == CalculatorMode.TIME) 38.sp else 32.sp,
                        ),
                        color = if (calculatorMode == CalculatorMode.TIME) Color(0xFF4CAF50) else Color(0xFFB8860B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(if (calculatorMode == CalculatorMode.TIME) 36.dp else 28.dp))
        val btnModifier = Modifier
            .height(64.dp)
            .padding(
                horizontal = if (calculatorMode == CalculatorMode.TIME) 2.dp else 4.dp,
                vertical = 2.dp
            )
        val buttonRows = if (calculatorMode == CalculatorMode.TIME) {
            listOf(
                listOf("⏰", "🕒", "C", "⌫"),
                listOf("7", "8", "9", "+"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", ":"),
                listOf("0", "", "=", "")
            )
        } else {
            listOf(
                listOf("⏰", "🕒", "C", "⌫"),
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "×"),
                listOf("1", "2", "3", "-"),
                listOf("0", ".", "=", "+")
            )
        }
        for (row in buttonRows) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (key in row) {
                    if (key.isEmpty()) {
                        // Empty space where no button should be
                        Spacer(modifier = btnModifier.weight(1f))
                    } else {
                        val isOp = key in listOf("÷", "×", "-", "+")
                        val isEq = key == "="
                        val isC = key == "C"
                        val isBack = key == "⌫"
                        val isTimeBtn = key == "⏰"
                        val isTimeCalcBtn = key == "🕒"
                        val bgColor = when {
                            isEq -> Color(0xFFB8860B)
                            isOp -> MaterialTheme.colorScheme.primary
                            isC -> MaterialTheme.colorScheme.error
                            isBack -> MaterialTheme.colorScheme.primary
                            isTimeBtn -> MaterialTheme.colorScheme.secondary
                            isTimeCalcBtn -> if (calculatorMode == CalculatorMode.TIME) Color(
                                0xFF4CAF50
                            ) else MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.surface
                        }
                        val contentColor = when {
                            isEq || isOp || isBack || isTimeBtn || isTimeCalcBtn -> MaterialTheme.colorScheme.onPrimary
                            isC -> Color.White
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> {
                                        var nextExp = expression
                                        var nextRes = result
                                        nextExp = ""; nextRes = ""
                                        setCalculatorState(nextExp, nextRes)
                                    }
                                    "⌫" -> {
                                        var nextExp = expression
                                        var nextRes = result
                                        nextExp =
                                            if (nextExp.isNotEmpty()) nextExp.dropLast(1) else ""
                                        setCalculatorState(nextExp, nextRes)
                                    }
                                    "=" -> {
                                        var nextExp = expression
                                        var nextRes = result
                                        if (calculatorMode == CalculatorMode.TIME) {
                                            nextRes = evaluateTimeExpression(nextExp)
                                        } else {
                                            nextRes = eval(nextExp)
                                        }
                                        setCalculatorState(nextExp, nextRes)
                                    }

                                    "⏰" -> {
                                        onTimeConversionClick()
                                    }
                                    "🕒" -> {
                                        setCalculatorState("", "") // Clear state on toggle
                                        onModeChange(if (calculatorMode == CalculatorMode.TIME) CalculatorMode.NORMAL else CalculatorMode.TIME)
                                    }
                                    else -> {
                                        var nextExp = expression
                                        var nextRes = result
                                        if (calculatorMode == CalculatorMode.TIME) {
                                            if (key in "0123456789") {
                                                // If no operator yet, build time digits
                                                val lastOpIdx =
                                                    nextExp.lastIndexOfAny(charArrayOf('+', '-'))
                                                if (lastOpIdx == -1) {
                                                    nextExp += key
                                                    // Limit entry to 4 digits before next operator
                                                    val digits = nextExp.filter { it.isDigit() }
                                                    if (digits.length > 4) nextExp =
                                                        digits.takeLast(4)
                                                } else {
                                                    // After an op, start next time part
                                                    val prefix = nextExp.substring(0..lastOpIdx)
                                                    val suffix = nextExp.substring(lastOpIdx + 1)
                                                    val newSuffix =
                                                        (suffix + key).filter { it.isDigit() }
                                                            .takeLast(4)
                                                    nextExp = prefix + newSuffix
                                                }
                                                setCalculatorState(nextExp, nextRes)
                                            } else if (key == ":") {
                                                // ignore direct : on input
                                            } else if (key in listOf("+", "-")) {
                                                // Only add op if last was a time block
                                                if (nextExp.isNotEmpty() && !nextExp.last()
                                                        .isWhitespace() && nextExp.last().isDigit()
                                                ) {
                                                    nextExp += key
                                                }
                                                setCalculatorState(nextExp, nextRes)
                                            } else {
                                                // =, etc. handled as previously
                                                // ... fall through to existing logic
                                                if (nextRes.isNotEmpty()) {
                                                    nextExp = key
                                                    nextRes = ""
                                                } else {
                                                    nextExp += key
                                                }
                                                setCalculatorState(nextExp, nextRes)
                                            }
                                        } else {
                                            // NORMAL MODE logic unchanged...
                                            if (nextRes.isNotEmpty()) {
                                                if (key in listOf("÷", "×", "-", "+")) {
                                                    nextExp = nextRes + key
                                                    nextRes = ""
                                                } else {
                                                    nextExp = key
                                                    nextRes = ""
                                                }
                                            } else {
                                                nextExp += key
                                            }
                                            setCalculatorState(nextExp, nextRes)
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = bgColor),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(8.dp),
                            modifier = btnModifier.weight(1f)
                        ) {
                            if (key == "⏰") {
                                Icon(
                                    Icons.Outlined.Schedule,
                                    contentDescription = "Time Conversion",
                                    tint = contentColor
                                )
                            } else if (key == "🕒") {
                                if (calculatorMode == CalculatorMode.NORMAL) {
                                    Icon(
                                        Icons.Outlined.Calculate,
                                        contentDescription = "Normal Calculator Mode",
                                        tint = contentColor
                                    )
                                } else {
                                    Icon(
                                        Icons.Outlined.Timer,
                                        contentDescription = "Time Calculator Mode",
                                        tint = contentColor
                                    )
                                }
                            } else {
                                Text(
                                    key,
                                    color = contentColor,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
