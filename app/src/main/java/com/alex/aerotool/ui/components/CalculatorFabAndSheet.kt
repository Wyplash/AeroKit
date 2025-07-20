package com.alex.aerotool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

@Composable
fun CalculatorFabAndSheet() {
    var showCalculator by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        if (showCalculator) {
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(Modifier.fillMaxSize()) {
                    AeroTopBar(
                        title = "AeroTool",
                        onBackClick = { showCalculator = false }
                    )
                    CalculatorContent()
                }
            }
        }
        if (!showCalculator) {
            FloatingActionButton(
                onClick = { showCalculator = true },
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

@Composable
private fun CalculatorContent() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

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
        Card(
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                Modifier.padding(horizontal = 22.dp, vertical = 14.dp)
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                if (result.isNotEmpty()) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = Color(0xFFB8860B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        val btnModifier = Modifier
            .height(64.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp)
        val buttonRows = listOf(
            listOf("C", "⌫"),
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+")
        )
        for (row in buttonRows) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (key in row) {
                    val isOp = key in listOf("÷", "×", "-", "+")
                    val isEq = key == "="
                    val isC = key == "C"
                    val isBack = key == "⌫"
                    val bgColor = when {
                        isEq -> Color(0xFFB8860B)
                        isOp -> MaterialTheme.colorScheme.primary
                        isC -> MaterialTheme.colorScheme.error
                        isBack -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surface
                    }
                    val contentColor = when {
                        isEq || isOp || isBack -> MaterialTheme.colorScheme.onPrimary
                        isC -> Color.White
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    Button(
                        onClick = {
                            when (key) {
                                "C" -> {
                                    expression = ""; result = ""
                                }

                                "⌫" -> {
                                    expression =
                                        if (expression.isNotEmpty()) expression.dropLast(1) else ""
                                }
                                "=" -> result = eval(expression)
                                else -> {
                                    if (result.isNotEmpty()) {
                                        if (key in listOf("÷", "×", "-", "+")) {
                                            expression = result + key
                                            result = ""
                                        } else {
                                            expression = key
                                            result = ""
                                        }
                                    } else {
                                        expression += key
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(8.dp),
                        modifier = btnModifier.weight(1f)
                    ) {
                        Text(key, color = contentColor, style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
