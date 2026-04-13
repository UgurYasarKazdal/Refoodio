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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract
import kotlin.math.roundToInt

/**
 * Tezgah ölçü seçimi bottom sheet.
 * Geliştirilmiş versiyon: Kategori bazlı akıllı birim filtreleme.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MeasurementBottomSheet(
    item: InventoryListContract.InventoryItemUiModel,
    currentTezgahQuantity: Double?,
    onTezgahAdd: (id: Int, quantity: Double) -> Unit,
    onRemoveFromTezgah: (id: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val maxQty = item.originalItem.quantity

    // Kullanıcının o an seçtiği geçici birim
    var selectedDisplayUnit by remember { mutableStateOf(item.unit) }

    // Miktar state'i (Her zaman ürünün ANA BİRİMİ cinsinden saklanır)
    var quantityInBaseUnit by remember(item.id) {
        val initial = currentTezgahQuantity ?: (if (item.unit == FoodUnit.KILOGRAM || item.unit == FoodUnit.LITER) maxQty else item.unit.step.coerceAtMost(maxQty))
        mutableDoubleStateOf(initial)
    }

    val baseUnitName = stringResource(item.unit.shortNameResId)
    val displayUnitName = stringResource(selectedDisplayUnit.shortNameResId)

    // Akıllı Tüketim Seçenekleri
    val consumptionUnitOptions = remember(item) {
        val options = mutableListOf(item.unit)
        when (item.unit.type) {
            FoodUnit.UnitType.WEIGHT -> {
                if (item.unit == FoodUnit.KILOGRAM) options.add(FoodUnit.GRAM)
                
                // Kaşık seçenekleri sadece toz/taneli akışkan kategorilerde
                if (item.originalItem.category in listOf(FoodCategory.SPICE, FoodCategory.SAUCE, FoodCategory.STAPLE_FOOD, FoodCategory.OIL, FoodCategory.SWEETENER)) {
                    options.addAll(listOf(FoodUnit.TABLESPOON, FoodUnit.TEASPOON, FoodUnit.TEA_SPOON))
                }
            }
            FoodUnit.UnitType.VOLUME -> {
                if (item.unit == FoodUnit.LITER) options.addAll(listOf(FoodUnit.MILLILITER, FoodUnit.CUP))
                options.addAll(listOf(FoodUnit.TABLESPOON, FoodUnit.TEASPOON, FoodUnit.TEA_SPOON))
            }
            FoodUnit.UnitType.COUNTABLE -> {
                // Sadece Paket ürünlerde (Kahve paketi gibi) kaşık desteği verelim
                if (item.unit == FoodUnit.PACK && item.originalItem.category in listOf(FoodCategory.BEVERAGE, FoodCategory.STAPLE_FOOD)) {
                    options.addAll(listOf(FoodUnit.TABLESPOON, FoodUnit.TEASPOON, FoodUnit.TEA_SPOON))
                }
            }
            else -> {}
        }
        options.distinct()
    }

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
            // Başlık
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
                        text = "Stok: ${"%.2f".format(maxQty)} $baseUnitName",
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
                            text = "Tezgahta: ${"%.2f".format(currentTezgahQuantity)} $baseUnitName",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider()

            // Birim Seçici
            if (consumptionUnitOptions.size > 1) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tüketim Birimi",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        consumptionUnitOptions.forEach { unit ->
                            val isSelected = selectedDisplayUnit == unit
                            if (isSelected) {
                                Button(onClick = { }, modifier = Modifier.height(36.dp)) {
                                    Text(stringResource(unit.fullNameResId), fontSize = 12.sp)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { selectedDisplayUnit = unit },
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(stringResource(unit.fullNameResId), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Kontrol alanı
            val controlType = if (selectedDisplayUnit != item.unit) TezgahControlType.COUNTABLE else item.unit.tezgahControlType()

            when (controlType) {
                TezgahControlType.COUNTABLE -> {
                    val stepInBaseUnit = if (selectedDisplayUnit != item.unit) {
                        when {
                            item.unit == FoodUnit.KILOGRAM && selectedDisplayUnit == FoodUnit.GRAM -> 0.001
                            item.unit == FoodUnit.LITER && selectedDisplayUnit == FoodUnit.MILLILITER -> 0.001
                            
                            item.unit == FoodUnit.GRAM -> when(selectedDisplayUnit) {
                                FoodUnit.TABLESPOON -> 15.0
                                FoodUnit.TEASPOON -> 5.0
                                FoodUnit.TEA_SPOON -> 2.5
                                else -> 1.0
                            }
                            item.unit == FoodUnit.KILOGRAM -> when(selectedDisplayUnit) {
                                FoodUnit.TABLESPOON -> 0.015
                                FoodUnit.TEASPOON -> 0.005
                                FoodUnit.TEA_SPOON -> 0.0025
                                else -> 0.01
                            }
                            item.unit == FoodUnit.LITER -> when(selectedDisplayUnit) {
                                FoodUnit.TABLESPOON -> 0.015
                                FoodUnit.TEASPOON -> 0.005
                                FoodUnit.TEA_SPOON -> 0.0025
                                else -> 0.01
                            }
                            else -> when (selectedDisplayUnit) {
                                FoodUnit.TABLESPOON -> 0.05
                                FoodUnit.TEASPOON -> 0.02
                                FoodUnit.TEA_SPOON -> 0.01
                                else -> item.unit.step
                            }
                        }
                    } else item.unit.step

                    StepperRow(
                        quantity = quantityInBaseUnit,
                        displayValue = (quantityInBaseUnit / stepInBaseUnit),
                        step = stepInBaseUnit,
                        maxQty = maxQty,
                        unitName = displayUnitName,
                        onDecrease = { quantityInBaseUnit = (quantityInBaseUnit - stepInBaseUnit).coerceAtLeast(stepInBaseUnit) },
                        onIncrease = { quantityInBaseUnit = (quantityInBaseUnit + stepInBaseUnit).coerceAtMost(maxQty) }
                    )
                }

                TezgahControlType.LIQUID -> {
                    val presets = buildLiquidPresets(item.unit, maxQty)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEach { (label, value) ->
                            val clamped = value.coerceAtMost(maxQty)
                            val isActive = "%.2f".format(quantityInBaseUnit) == "%.2f".format(clamped)
                            if (isActive) Button(onClick = { }) { Text(label) }
                            else OutlinedButton(onClick = { quantityInBaseUnit = clamped }) { Text(label) }
                        }
                    }
                    StepperRow(
                        quantity = quantityInBaseUnit, displayValue = quantityInBaseUnit,
                        step = 0.1, maxQty = maxQty, unitName = baseUnitName,
                        onDecrease = { quantityInBaseUnit = (quantityInBaseUnit - 0.1).coerceAtLeast(0.1) },
                        onIncrease = { quantityInBaseUnit = (quantityInBaseUnit + 0.1).coerceAtMost(maxQty) }
                    )
                }

                TezgahControlType.BULK -> {
                    val ratios = listOf("¼" to 0.25, "½" to 0.5, "¾" to 0.75, "Tamamı" to 1.0)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ratios.forEach { (label, ratio) ->
                            val target = (maxQty * ratio).let {
                                if (item.unit == FoodUnit.KILOGRAM) (it * 100).roundToInt() / 100.0
                                else (it / item.unit.step).roundToInt() * item.unit.step
                            }.coerceIn(0.01, maxQty)
                            val isActive = "%.2f".format(quantityInBaseUnit) == "%.2f".format(target)
                            if (isActive) Button(onClick = { }, modifier = Modifier.weight(1f)) { Text(label, fontSize = 12.sp) }
                            else OutlinedButton(onClick = { quantityInBaseUnit = target }, modifier = Modifier.weight(1f)) { Text(label, fontSize = 12.sp) }
                        }
                    }
                    StepperRow(
                        quantity = quantityInBaseUnit, displayValue = quantityInBaseUnit,
                        step = if (item.unit == FoodUnit.KILOGRAM) 0.05 else item.unit.step,
                        maxQty = maxQty, unitName = baseUnitName,
                        onDecrease = { quantityInBaseUnit = (quantityInBaseUnit - (if (item.unit == FoodUnit.KILOGRAM) 0.05 else item.unit.step)).coerceAtLeast(0.01) },
                        onIncrease = { quantityInBaseUnit = (quantityInBaseUnit + (if (item.unit == FoodUnit.KILOGRAM) 0.05 else item.unit.step)).coerceAtMost(maxQty) }
                    )
                }

                TezgahControlType.PACK -> {
                    StepperRow(
                        quantity = quantityInBaseUnit, displayValue = quantityInBaseUnit,
                        step = item.unit.step, maxQty = maxQty, unitName = baseUnitName,
                        onDecrease = { quantityInBaseUnit = (quantityInBaseUnit - item.unit.step).coerceAtLeast(item.unit.step) },
                        onIncrease = { quantityInBaseUnit = (quantityInBaseUnit + item.unit.step).coerceAtMost(maxQty) }
                    )
                }
            }

            HorizontalDivider()

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentTezgahQuantity != null) {
                    OutlinedButton(
                        onClick = { onRemoveFromTezgah(item.id); onDismiss() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Çıkar") }
                }
                FilledTonalButton(onClick = { onTezgahAdd(item.id, maxQty); onDismiss() }, modifier = Modifier.weight(1f)) { Text("Hepsini Kullan") }
                Button(onClick = { onTezgahAdd(item.id, quantityInBaseUnit); onDismiss() }, modifier = Modifier.weight(1.2f)) { Text(if (currentTezgahQuantity == null) "Tezgaha Ekle" else "Güncelle") }
            }
        }
    }
}

@Composable
private fun StepperRow(
    quantity: Double,
    displayValue: Double,
    step: Double,
    maxQty: Double,
    unitName: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        FilledTonalIconButton(onClick = onDecrease, enabled = quantity > 0.001, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Remove, contentDescription = "Azalt") }
        Spacer(modifier = Modifier.width(24.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (displayValue >= 1.0) "%.1f".format(displayValue) else "%.2f".format(displayValue),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = unitName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(24.dp))
        FilledTonalIconButton(onClick = onIncrease, enabled = quantity < maxQty, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Add, contentDescription = "Artır") }
    }
}

private enum class TezgahControlType {
    COUNTABLE, LIQUID, BULK, PACK
}

private fun FoodUnit.tezgahControlType(): TezgahControlType {
    return when (this) {
        FoodUnit.PIECE, FoodUnit.BUNCH, FoodUnit.CUP, FoodUnit.TABLESPOON, FoodUnit.TEASPOON, FoodUnit.TEA_SPOON -> TezgahControlType.COUNTABLE
        FoodUnit.LITER, FoodUnit.MILLILITER -> TezgahControlType.LIQUID
        FoodUnit.GRAM, FoodUnit.KILOGRAM -> TezgahControlType.BULK
        FoodUnit.PACK -> TezgahControlType.PACK
    }
}

private fun buildCountablePresets(maxQty: Double, unit: FoodUnit): List<Double> {
    val options = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 10.0, 12.0)
    return options.filter { it <= maxQty && it >= unit.step }
}

private fun buildLiquidPresets(unit: FoodUnit, maxQty: Double): List<Pair<String, Double>> {
    return if (unit == FoodUnit.LITER) {
        listOf("250ml" to 0.25, "500ml" to 0.5, "1L" to 1.0, "1.5L" to 1.5, "2L" to 2.0)
    } else {
        listOf("100ml" to 100.0, "200ml" to 200.0, "250ml" to 250.0, "500ml" to 500.0)
    }.filter { it.second <= maxQty }
}
