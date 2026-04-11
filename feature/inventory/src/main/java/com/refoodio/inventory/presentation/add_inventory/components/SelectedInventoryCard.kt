package com.refoodio.inventory.presentation.add_inventory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.ui.components.RefoodioDropdown
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.core.ui.util.formatQuantity
import com.refoodio.inventory.R

private val UNIT_ORDER = listOf(
    FoodUnit.PIECE,
    FoodUnit.GRAM,
    FoodUnit.KILOGRAM,
    FoodUnit.LITER,
    FoodUnit.MILLILITER,
    FoodUnit.BUNCH,
    FoodUnit.CUP,
    FoodUnit.TABLESPOON,
    FoodUnit.TEASPOON,
    FoodUnit.PACK
)

@Composable
fun SelectedInventoryCard(
    selectedFoodName: String,
    selectedCategory: String,
    quantity: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    selectedUnit: FoodUnit,
    onUnitSelected: (FoodUnit) -> Unit,
    onQuantitySelected: (Double) -> Unit,
    packageContent: Double?,
    packageContentUnit: FoodUnit,
    onPackageContentChanged: (Double?) -> Unit,
    onPackageContentUnitSelected: (FoodUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(RefoodioTheme.spacing.large)) {
            Text(
                text = stringResource(
                    R.string.add_inventory_selected_label, selectedFoodName
                ), style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(
                    R.string.add_inventory_category_label, selectedCategory
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(RefoodioTheme.spacing.medium))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)
            ) {
                IconButton(onClick = onDecrement) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = stringResource(R.string.add_inventory_decrease_qty),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                RefoodioDropdown(
                    selectedUnit.generateScale(),
                    selectedItem = quantity,
                    onItemSelected = { onQuantitySelected(it) },
                    itemLabel = { it.formatQuantity() },
                )

                IconButton(onClick = onIncrement) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = stringResource(R.string.add_inventory_increase_qty),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(RefoodioTheme.spacing.medium))

                RefoodioDropdown(
                    items = UNIT_ORDER,
                    selectedItem = selectedUnit,
                    onItemSelected = { onUnitSelected(it) },
                    itemLabel = { stringResource(id = it.fullNameResId) })
            }

            // Paket içeriği — sadece PACK seçiliyken göster
            if (selectedUnit == FoodUnit.PACK) {
                Spacer(modifier = Modifier.height(RefoodioTheme.spacing.medium))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(RefoodioTheme.spacing.medium))

                Text(
                    text = "Bu pakette ne kadar var? (isteğe bağlı)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(RefoodioTheme.spacing.small))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)
                ) {
                    OutlinedTextField(
                        value = packageContent?.let { "%.10g".format(it).trimEnd('0').trimEnd('.') } ?: "",
                        onValueChange = { raw ->
                            onPackageContentChanged(raw.toDoubleOrNull()?.takeIf { it > 0 })
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Örn: 20, 500, 1",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    RefoodioDropdown(
                        items = UNIT_ORDER.filter { it != FoodUnit.PACK },
                        selectedItem = packageContentUnit,
                        onItemSelected = { onPackageContentUnitSelected(it) },
                        itemLabel = { stringResource(id = it.fullNameResId) }
                    )
                }

                if (packageContent != null && packageContent > 0.0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kaydedilecek: ${"%.10g".format(packageContent).trimEnd('0').trimEnd('.')} ${stringResource(packageContentUnit.fullNameResId)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
