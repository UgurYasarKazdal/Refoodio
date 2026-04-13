package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

/**
 * Tezgah ölçü seçimi bottom sheet.
 *
 * - COUNTABLE (PIECE, BUNCH, CUP, TABLESPOON, TEASPOON): stepper + preset bubbles
 * - LIQUID (LITER, MILLILITER): hızlı preset bubbles + stepper
 * - BULK (GRAM, KILOGRAM): oran butonları (¼ ½ Tamamı) + stepper
 * - PACK: sadece stepper (kaç paket)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MeasurementBottomSheet(
    item: InventoryListContract.InventoryItemUiModel,
    currentTezgahQuantity: Double?,          // null → tezgahta yok
    onTezgahAdd: (id: Int, quantity: Double) -> Unit,
    onRemoveFromTezgah: (id: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val maxQty = item.originalItem.quantity

    // Başlangıç miktarı: zaten tezgahtaysa onun miktarı, yoksa 1 adım
    var quantity by remember(item.id) {
        mutableDoubleStateOf(currentTezgahQuantity ?: item.unit.step.coerceAtMost(maxQty))
    }

    val unitName = stringResource(item.unit.shortNameResId)
    val controlType = item.unit.tezgahControlType()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Başlık ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "Stok: ${"%.1f".format(maxQty)} $unitName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (currentTezgahQuantity != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Tezgahta: ${"%.1f".format(currentTezgahQuantity)} $unitName",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider()

            // ── Kontrol alanı ───────────────────────────────────────────────
            when (controlType) {
                TezgahControlType.COUNTABLE -> {
                    // Hızlı preset bubbles
                    val presets = buildCountablePresets(maxQty, item.unit)
                    if (presets.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presets.forEach { preset ->
                                val isActive = quantity == preset
                                if (isActive) {
                                    Button(onClick = { }) { Text("${"%.0f".format(preset)} $unitName") }
                                } else {
                                    OutlinedButton(onClick = { quantity = preset }) {
                                        Text("${"%.0f".format(preset)} $unitName")
                                    }
                                }
                            }
                        }
                    }
                    StepperRow(
                        quantity = quantity,
                        step = item.unit.step,
                        maxQty = maxQty,
                        unitName = unitName,
                        onDecrease = { quantity = (quantity - item.unit.step).coerceAtLeast(item.unit.step) },
                        onIncrease = { quantity = (quantity + item.unit.step).coerceAtMost(maxQty) }
                    )
                }

                TezgahControlType.LIQUID -> {
                    // Preset miktar bubbles
                    val presets = buildLiquidPresets(item.unit, maxQty)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { (label, value) ->
                            val clamped = value.coerceAtMost(maxQty)
                            val isActive = quantity == clamped
                            if (isActive) {
                                Button(onClick = { }) { Text(label) }
                            } else {
                                OutlinedButton(
                                    onClick = { quantity = clamped },
                                    enabled = clamped > 0.0
                                ) { Text(label) }
                            }
                        }
                    }
                    StepperRow(
                        quantity = quantity,
                        step = item.unit.step,
                        maxQty = maxQty,
                        unitName = unitName,
                        onDecrease = { quantity = (quantity - item.unit.step).coerceAtLeast(item.unit.step) },
                        onIncrease = { quantity = (quantity + item.unit.step).coerceAtMost(maxQty) }
                    )
                }

                TezgahControlType.BULK -> {
                    // Oran butonları
                    val ratios = listOf(
                        "¼" to 0.25,
                        "½" to 0.5,
                        "¾" to 0.75,
                        "Tamamı" to 1.0
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ratios.forEach { (label, ratio) ->
                            val target = (maxQty * ratio).let {
                                // Round to nearest step
                                val steps = (it / item.unit.step).toLong()
                                (steps * item.unit.step).coerceIn(item.unit.step, maxQty)
                            }
                            val isActive = quantity == target
                            if (isActive) {
                                Button(
                                    onClick = { },
                                    modifier = Modifier.weight(1f)
                                ) { Text(label, fontSize = 12.sp) }
                            } else {
                                OutlinedButton(
                                    onClick = { quantity = target },
                                    modifier = Modifier.weight(1f)
                                ) { Text(label, fontSize = 12.sp) }
                            }
                        }
                    }
                    StepperRow(
                        quantity = quantity,
                        step = item.unit.step,
                        maxQty = maxQty,
                        unitName = unitName,
                        onDecrease = { quantity = (quantity - item.unit.step).coerceAtLeast(item.unit.step) },
                        onIncrease = { quantity = (quantity + item.unit.step).coerceAtMost(maxQty) }
                    )
                }

                TezgahControlType.PACK -> {
                    // Sadece stepper — kaç paket
                    StepperRow(
                        quantity = quantity,
                        step = item.unit.step,
                        maxQty = maxQty,
                        unitName = unitName,
                        onDecrease = { quantity = (quantity - item.unit.step).coerceAtLeast(item.unit.step) },
                        onIncrease = { quantity = (quantity + item.unit.step).coerceAtMost(maxQty) }
                    )
                }
            }

            HorizontalDivider()

            // ── Aksiyonlar ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tezgahtan çıkar (sadece zaten eklenmişse)
                if (currentTezgahQuantity != null) {
                    OutlinedButton(
                        onClick = {
                            onRemoveFromTezgah(item.id)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Çıkar")
                    }
                }

                // Hepsini Kullan — tam miktarla ekle
                FilledTonalButton(
                    onClick = {
                        onTezgahAdd(item.id, maxQty)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tamamı")
                }

                // Tezgaha At — seçilen miktar
                Button(
                    onClick = {
                        if (quantity > 0.0) {
                            onTezgahAdd(item.id, quantity)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(if (currentTezgahQuantity != null) 1f else 2f),
                    enabled = quantity > 0.0
                ) {
                    Text("Tezgaha At")
                }
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

// ── Stepper satırı ────────────────────────────────────────────────────────────

@Composable
private fun StepperRow(
    quantity: Double,
    step: Double,
    maxQty: Double,
    unitName: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onDecrease,
            enabled = quantity > step,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Azalt")
        }

        Spacer(Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%.1f".format(quantity),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unitName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.width(16.dp))

        FilledTonalIconButton(
            onClick = onIncrease,
            enabled = quantity < maxQty,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Artır")
        }
    }
}

// ── Kontrol tipi ──────────────────────────────────────────────────────────────

private enum class TezgahControlType { COUNTABLE, LIQUID, BULK, PACK }

private fun FoodUnit.tezgahControlType(): TezgahControlType = when (this) {
    FoodUnit.PIECE, FoodUnit.BUNCH, FoodUnit.CUP,
    FoodUnit.TABLESPOON, FoodUnit.TEASPOON -> TezgahControlType.COUNTABLE
    FoodUnit.LITER, FoodUnit.MILLILITER    -> TezgahControlType.LIQUID
    FoodUnit.GRAM, FoodUnit.KILOGRAM       -> TezgahControlType.BULK
    FoodUnit.PACK                          -> TezgahControlType.PACK
}

// ── Preset yardımcıları ───────────────────────────────────────────────────────

private fun buildCountablePresets(maxQty: Double, unit: FoodUnit): List<Double> {
    val candidates = when (unit) {
        FoodUnit.CUP, FoodUnit.TABLESPOON, FoodUnit.TEASPOON ->
            listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        else ->
            listOf(1.0, 2.0, 3.0, 5.0, 10.0)
    }
    return candidates.filter { it <= maxQty }
}

private fun buildLiquidPresets(unit: FoodUnit, maxQty: Double): List<Pair<String, Double>> {
    return when (unit) {
        FoodUnit.MILLILITER -> listOf(
            "50 ml" to 50.0,
            "100 ml" to 100.0,
            "200 ml" to 200.0,
            "500 ml" to 500.0,
            "1 L" to 1000.0
        )
        else -> listOf( // LITER
            "0.25 L" to 0.25,
            "0.5 L" to 0.5,
            "1 L" to 1.0,
            "2 L" to 2.0
        )
    }.filter { it.second <= maxQty + 0.001 }
}
