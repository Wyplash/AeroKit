package com.alex.aerotool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt
import kotlin.math.abs
import com.alex.aerotool.ui.theme.ThemeController
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.absoluteValue

fun Double.roundMostVol(n: Int = 4): String = "% .${n}f".format(this).trim()

@Composable
fun VolumeConversionScreen(
    themeController: ThemeController,
    onBack: (() -> Unit)? = null,
    showInfo: Boolean = false,
    onInfoDismiss: (() -> Unit)? = null
) {
    data class UnitDef(
        val key: String,
        val label: String,
        val emoji: String,
        val abbr: String,
        val toBase: (Double) -> Double,   // convert to liters
        val fromBase: (Double) -> Double, // convert from liters
    )

    val unitDefs = listOf(
        UnitDef("L", "Liters", "💧", "L", { it }, { it }),
        UnitDef("gal", "Gallons", "🛢", "gal", { it * 3.78541 }, { it / 3.78541 }),
        UnitDef("qt", "Quarts", "🥛", "qt", { it * 0.946353 }, { it / 0.946353 }),
        UnitDef("pt", "Pints", "🥃", "pt", { it * 0.473176 }, { it / 0.473176 }),
        UnitDef("cup", "Cups", "☕", "cup", { it * 0.236588 }, { it / 0.236588 }),
        UnitDef("floz", "Fl Oz", "🥄", "fl oz", { it * 0.0295735 }, { it / 0.0295735 }),
        UnitDef("mL", "MilliLiters", "🧬", "mL", { it / 1000.0 }, { it * 1000.0 }),
    )

    data class UnitCard(var unitKey: String, var value: String)

    var unitCards by remember {
        mutableStateOf(
            listOf(
                UnitCard("L", "1.5"),
                UnitCard("gal", "0.396"),
            )
        )
    }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    fun moveItem(from: Int, to: Int) {
        if (from == to) return
        val item = unitCards[from]
        val list = unitCards.toMutableList()
        list.removeAt(from)
        list.add(to, item)
        unitCards = list
    }

    fun recalcAll(fromIdx: Int, text: String) {
        val fromCard = unitCards.getOrNull(fromIdx) ?: return
        val fromDef = unitDefs.first { it.key == fromCard.unitKey }
        val fromValue = text.toDoubleOrNull() ?: return
        val base = fromDef.toBase(fromValue)
        unitCards = unitCards.mapIndexed { idx, card ->
            val def = unitDefs.first { it.key == card.unitKey }
            if (idx == fromIdx) card.copy(value = text)
            else card.copy(value = if (text.isBlank()) "" else def.fromBase(base).roundMostVol(4))
        }
    }

    fun addUnitCard() {
        val unused = unitDefs.map { it.key } - unitCards.map { it.unitKey }
        if (unused.isEmpty()) return
        val baseIdx = unitCards.indexOfFirst { it.value.isNotBlank() }
        val newKey = unused.first()
        val card = if (baseIdx != -1) {
            val baseCard = unitCards[baseIdx]
            val baseValue = baseCard.value.toDoubleOrNull() ?: 0.0
            val baseDef = unitDefs.first { it.key == baseCard.unitKey }
            val base = baseDef.toBase(baseValue)
            val def = unitDefs.first { it.key == newKey }
            UnitCard(
                newKey,
                if (baseCard.value.isBlank()) "" else def.fromBase(base).roundMostVol(4)
            )
        } else UnitCard(newKey, "")
        unitCards = unitCards + card
        val idxToUpdate = unitCards.indexOfFirst { it.value.isNotBlank() }
        if (idxToUpdate != -1) {
            val text = unitCards[idxToUpdate].value
            recalcAll(idxToUpdate, text)
        }
    }

    fun removeCard(idx: Int) {
        if (unitCards.size <= 2) return
        unitCards = unitCards.filterIndexed { i, _ -> i != idx }
    }

    fun changeCardUnit(idx: Int, key: String) {
        if (unitCards.any { it.unitKey == key }) return
        unitCards = unitCards.mapIndexed { i, card ->
            if (i == idx) card.copy(unitKey = key, value = "") else card
        }
    }

    var showUnitPicker by remember { mutableStateOf(false) }
    var unitSearch by remember { mutableStateOf("") }
    var activeUnitPickerIdx by remember { mutableStateOf<Int?>(null) }
    var activeUnitSearch by remember { mutableStateOf("") }
    val availableUnits = unitDefs.filter { def -> unitCards.none { it.unitKey == def.key } }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(11.dp))
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Volume",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Enter a value in any volume unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(unitCards, key = { _, it -> it.unitKey }) { idx, card ->
                    val unit = unitDefs.first { it.key == card.unitKey }
                    val isFirst = idx == 0
                    val trashRevealOffset = 90f
                    var cardOpen by remember(card.unitKey) { mutableStateOf(false) }
                    var swipeOffset by remember(card.unitKey) { mutableStateOf(0f) }
                    var actuallyDeleting by remember { mutableStateOf(false) }
                    val dragProgress = (swipeOffset / trashRevealOffset).coerceIn(0f, 1f)
                    val canDelete = unitCards.size > 2
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp)
                    ) {
                        if (canDelete && (cardOpen || swipeOffset > 4f)) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.CenterStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier
                                        .size((36 + 12 * dragProgress).dp)
                                        .background(
                                            MaterialTheme.colorScheme.error.copy(
                                                alpha = 0.19f + 0.12f * dragProgress
                                            ),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error.copy(
                                            alpha = 0.7f + 0.3f * dragProgress
                                        ),
                                        modifier = Modifier
                                            .size((22 + 8 * dragProgress).dp)
                                            .clickable(enabled = !actuallyDeleting) {
                                                actuallyDeleting = true
                                                removeCard(idx)
                                                cardOpen = false
                                                swipeOffset = 0f
                                                actuallyDeleting = false
                                            }
                                    )
                                }
                            }
                        }
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .offset {
                                    IntOffset(
                                        (if (cardOpen) trashRevealOffset else swipeOffset).roundToInt(),
                                        0
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 5.dp)
                                .background(
                                    if (cardOpen || swipeOffset > 0f)
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.13f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(20.dp)
                                )
                                .then(
                                    if (canDelete)
                                        Modifier.pointerInput(card.unitKey, unitCards.size) {
                                            detectDragGestures(
                                                onDragStart = {},
                                                onDrag = { change, dragAmount ->
                                                    change.consumeAllChanges()
                                                    if (cardOpen && dragAmount.x < 0) {
                                                        val closingOffset =
                                                            (trashRevealOffset + dragAmount.x).coerceIn(
                                                                0f,
                                                                trashRevealOffset
                                                            )
                                                        swipeOffset = closingOffset
                                                    } else if (!cardOpen && dragAmount.x > 0 && dragAmount.y.absoluteValue < 30f) {
                                                        val openOffset =
                                                            (swipeOffset + dragAmount.x).coerceIn(
                                                                0f,
                                                                trashRevealOffset
                                                            )
                                                        swipeOffset = openOffset
                                                    }
                                                },
                                                onDragEnd = {
                                                    if ((!cardOpen && swipeOffset > trashRevealOffset * 0.5f) || (cardOpen && swipeOffset > trashRevealOffset * 0.5f)) {
                                                        cardOpen = true
                                                        swipeOffset = trashRevealOffset
                                                    } else {
                                                        cardOpen = false
                                                        swipeOffset = 0f
                                                    }
                                                },
                                                onDragCancel = {
                                                    swipeOffset =
                                                        if (cardOpen) trashRevealOffset else 0f
                                                }
                                            )
                                        }
                                    else Modifier
                                ),
                            shape = RoundedCornerShape(20.dp),
                            border = if (isFirst) androidx.compose.foundation.BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.outline
                            ) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = if (cardOpen || swipeOffset > 0f) 10.dp else 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .heightIn(min = 64.dp)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    Modifier.padding(start = 5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Move",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column(
                                    Modifier
                                        .weight(2.2f)
                                        .padding(start = 12.dp, end = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            unit.emoji,
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            unit.label,
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.clickable {
                                                activeUnitPickerIdx = idx; activeUnitSearch = ""
                                            }
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clickable {
                                                    activeUnitPickerIdx = idx; activeUnitSearch = ""
                                                }
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    var fieldValue by remember { mutableStateOf(card.value) }
                                    Box(
                                        Modifier
                                            .fillMaxWidth(0.46f)
                                            .height(56.dp)
                                    ) {
                                        Row(
                                            Modifier.matchParentSize(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = fieldValue,
                                                onValueChange = {
                                                    fieldValue = it; recalcAll(idx, it)
                                                },
                                                label = null,
                                                singleLine = true,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(),
                                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                    textAlign = TextAlign.End,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                keyboardOptions = KeyboardOptions.Default.copy(
                                                    imeAction = ImeAction.Done
                                                ),
                                                keyboardActions = KeyboardActions(onDone = {
                                                    recalcAll(idx, fieldValue)
                                                })
                                            )
                                            Text(
                                                unit.abbr,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier
                                                    .padding(start = 8.dp, end = 6.dp)
                                                    .widthIn(min = 32.dp)
                                                    .align(Alignment.CenterVertically),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (activeUnitPickerIdx == idx) {
                            val availableUnits =
                                unitDefs.filter { def -> unitCards.none { it.unitKey == def.key } || def.key == card.unitKey }
                            AlertDialog(
                                onDismissRequest = { activeUnitPickerIdx = null },
                                title = { Text("Units") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value = activeUnitSearch,
                                            onValueChange = { activeUnitSearch = it },
                                            label = { Text("Search units") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        val filtered = availableUnits.filter {
                                            it.label.contains(
                                                activeUnitSearch,
                                                ignoreCase = true
                                            )
                                        }
                                        LazyColumn(Modifier.heightIn(max = 350.dp)) {
                                            itemsIndexed(filtered) { _, u ->
                                                Row(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            changeCardUnit(idx, u.key)
                                                            activeUnitPickerIdx = null
                                                            activeUnitSearch = ""
                                                        }
                                                        .padding(vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        u.emoji,
                                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                                        modifier = Modifier.padding(end = 12.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            u.label,
                                                            style = MaterialTheme.typography.bodyLarge
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                confirmButton = {},
                                dismissButton = {
                                    TextButton(onClick = {
                                        activeUnitPickerIdx = null
                                    }) { Text("Cancel") }
                                }
                            )
                        }
                    }
                }
                item {
                    Row(
                        Modifier
                            .padding(start = 24.dp, top = 4.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "↳",
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 3.dp)
                        )
                        Text(
                            "Drag to reorder",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (availableUnits.isNotEmpty()) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = { showUnitPicker = true },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .padding(vertical = 3.dp)
                                    .size(64.dp)
                            ) {
                                Text("+", style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                    }
                }
            }
        }
    }
    if (showUnitPicker) {
        val availableUnits = unitDefs.filter { def -> unitCards.none { it.unitKey == def.key } }
        AlertDialog(
            onDismissRequest = { showUnitPicker = false },
            title = { Text("Units") },
            text = {
                Column {
                    OutlinedTextField(
                        value = unitSearch,
                        onValueChange = { unitSearch = it },
                        label = { Text("Search units") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    val filtered =
                        availableUnits.filter { it.label.contains(unitSearch, ignoreCase = true) }
                    LazyColumn(Modifier.heightIn(max = 350.dp)) {
                        itemsIndexed(filtered) { i, u ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        unitCards = unitCards + UnitCard(u.key, "")
                                        showUnitPicker = false
                                        unitSearch = ""
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    u.emoji,
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(u.label, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showUnitPicker = false }) { Text("Cancel") }
            }
        )
    }
}