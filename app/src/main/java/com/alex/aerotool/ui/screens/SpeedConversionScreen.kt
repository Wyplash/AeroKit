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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.roundToInt
import kotlin.math.abs
import com.alex.aerotool.ui.theme.ThemeController
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.absoluteValue
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import kotlin.math.round

@Serializable
data class PersistedSpeedUnitCard(val unitKey: String, val value: String)

val Context.speedUnitCardsDataStore by preferencesDataStore("speed_unit_cards")
val SPEED_UNIT_CARDS_KEY = stringPreferencesKey("speed_unit_cards")

fun Double.roundMost(n: Int = 4): String =
    if (n == 0) toInt().toString() else "%.${n}f".format(this).trim()

@Composable
fun SpeedConversionScreen(
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
        val toBase: (Double) -> Double,   // convert to m/s
        val fromBase: (Double) -> Double, // convert from m/s
    )

    val unitDefs = listOf(
        UnitDef("kt", "Knots", "⛴", "kt", { it * 0.514444 }, { it / 0.514444 }),
        UnitDef("mph", "Miles/hour", "⛴", "mph", { it * 0.44704 }, { it / 0.44704 }),
        UnitDef("kmh", "Km/hour", "⛴", "km/h", { it * 0.277778 }, { it / 0.277778 }),
        UnitDef("mps", "Meters/sec", "⛴", "m/s", { it }, { it }),
        UnitDef("fps", "Feet/sec", "⛴", "ft/s", { it * 0.3048 }, { it / 0.3048 }),
        UnitDef("mach", "Mach (SL)", "⛴", "mach", { it * 343.0 }, { it / 343.0 })
    )

    data class UnitCard(var unitKey: String, var value: String)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var unitCards by remember {
        mutableStateOf(
            listOf(
                UnitCard("kt", "250"),
                UnitCard("mph", "288.2")
            )
        )
    }
    var isRestored by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.speedUnitCardsDataStore.data.first()
        val cardsString = prefs[SPEED_UNIT_CARDS_KEY]
        if (cardsString != null) {
            try {
                val persisted: List<PersistedSpeedUnitCard> = Json.decodeFromString(cardsString)
                unitCards = persisted.map { UnitCard(it.unitKey, it.value) }
            } catch (_: Exception) {
            }
        }
        isRestored = true
    }

    fun persistCards(newList: List<UnitCard>) {
        scope.launch {
            context.speedUnitCardsDataStore.edit { prefs ->
                prefs[SPEED_UNIT_CARDS_KEY] =
                    Json.encodeToString(newList.map {
                        PersistedSpeedUnitCard(
                            it.unitKey,
                            it.value
                        )
                    })
            }
        }
    }

    fun setUnitCards(newList: List<UnitCard>) {
        unitCards = newList
        persistCards(newList)
    }

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    fun moveItem(from: Int, to: Int) {
        if (from == to) return
        val item = unitCards[from]
        val list = unitCards.toMutableList()
        list.removeAt(from)
        list.add(to, item)
        setUnitCards(list)
    }

    fun recalcAll(fromIdx: Int, text: String) {
        val fromCard = unitCards.getOrNull(fromIdx) ?: return
        val fromDef = unitDefs.first { it.key == fromCard.unitKey }
        val fromValue = text.toDoubleOrNull() ?: return
        val base = fromDef.toBase(fromValue)
        setUnitCards(unitCards.mapIndexed { idx, card ->
            val def = unitDefs.first { it.key == card.unitKey }
            if (idx == fromIdx) card.copy(value = text)
            else card.copy(value = if (text.isBlank()) "" else def.fromBase(base).roundMost(4))
        })
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
            UnitCard(newKey, if (baseCard.value.isBlank()) "" else def.fromBase(base).roundMost(4))
        } else UnitCard(newKey, "")
        setUnitCards(unitCards + card)
        val idxToUpdate = unitCards.indexOfFirst { it.value.isNotBlank() }
        if (idxToUpdate != -1) {
            val text = unitCards[idxToUpdate].value
            recalcAll(idxToUpdate, text)
        }
    }

    fun removeCard(idx: Int) {
        if (unitCards.size <= 2) return
        setUnitCards(unitCards.filterIndexed { i, _ -> i != idx })
    }

    fun changeCardUnit(idx: Int, key: String) {
        if (unitCards.any { it.unitKey == key }) return
        setUnitCards(unitCards.mapIndexed { i, card ->
            if (i == idx) card.copy(unitKey = key, value = "") else card
        })
    }

    var showUnitPicker by remember { mutableStateOf(false) }
    var unitSearch by remember { mutableStateOf("") }
    var activeUnitPickerIdx by remember { mutableStateOf<Int?>(null) }
    var activeUnitSearch by remember { mutableStateOf("") }
    val availableUnits = unitDefs.filter { def -> unitCards.none { it.unitKey == def.key } }

    // Info dialog for speed conversion help
    if (showInfo) {
        AlertDialog(
            onDismissRequest = { onInfoDismiss?.invoke() },
            title = { Text("Speed Conversion Tool") },
            text = {
                Text(
                    "Convert between various speed units commonly used in aviation.\n\n" +
                            "• Enter a value in any speed unit field\n" +
                            "• All other units update automatically\n" +
                            "• Drag cards using the menu icon to reorder\n" +
                            "• Swipe cards left to delete (minimum 2 required)\n" +
                            "• Click unit names to change the unit type\n" +
                            "• Add more units using the + button\n\n" +
                            "Common Aviation Speeds:\n" +
                            "• Knots (kt) - Standard aviation unit\n" +
                            "• Mach - Speed relative to sound (at sea level)\n" +
                            "• Miles/hour (mph) - Ground speed reference"
                )
            },
            confirmButton = {
                TextButton(onClick = { onInfoDismiss?.invoke() }) {
                    Text("OK")
                }
            }
        )
    }

    LaunchedEffect(unitCards.size) {
        if (unitCards.size > 2) {
            val idxToUpdate = unitCards.indexOfFirst { it.value.isNotBlank() }
            if (idxToUpdate != -1) {
                val text = unitCards[idxToUpdate].value
                recalcAll(idxToUpdate, text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(11.dp))
            Icon(
                imageVector = Icons.Filled.Speed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Speed",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Enter a value in any speed unit",
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
                                    Modifier
                                        .padding(start = 5.dp)
                                        .pointerInput(idx, draggedIndex) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    draggedIndex = idx; dragOffsetY = 0f
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consumeAllChanges()
                                                    dragOffsetY += dragAmount.y
                                                    val swapIdx = when {
                                                        dragAmount.y < 0 && idx > 0 && abs(
                                                            dragOffsetY
                                                        ) > 50 -> idx - 1

                                                        dragAmount.y > 0 && idx < unitCards.lastIndex && abs(
                                                            dragOffsetY
                                                        ) > 50 -> idx + 1

                                                        else -> null
                                                    }
                                                    if (swapIdx != null) {
                                                        moveItem(idx, swapIdx)
                                                        draggedIndex = swapIdx
                                                        dragOffsetY = 0f
                                                    }
                                                },
                                                onDragEnd = {
                                                    draggedIndex = null; dragOffsetY = 0f
                                                },
                                                onDragCancel = {
                                                    draggedIndex = null; dragOffsetY = 0f
                                                }
                                            )
                                        },
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
                                    // Per-card field value, robust pattern
                                    var fieldValue by remember(card.unitKey) { mutableStateOf(card.value) }
                                    LaunchedEffect(card.value) {
                                        if (card.value != fieldValue) fieldValue = card.value
                                    }
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
                                                    fieldValue = it
                                                    setUnitCards(unitCards.mapIndexed { i, c ->
                                                        if (i == idx) c.copy(value = it) else c
                                                    })
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
                                                    keyboardType = KeyboardType.Decimal,
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
                                        setUnitCards(unitCards + UnitCard(u.key, ""))
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