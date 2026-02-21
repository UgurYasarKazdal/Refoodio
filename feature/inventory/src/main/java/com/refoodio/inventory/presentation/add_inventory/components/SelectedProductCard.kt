package com.refoodio.inventory.presentation.add_inventory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.ui.components.RefoodioDropdown
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.core.ui.util.formatQuantity
import com.refoodio.inventory.R

@Composable
fun SelectedProductCard(
    selectedFoodName: String,
    selectedCategory: String,
    quantity: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    selectedUnit: FoodUnit,
    onUnitSelected: (FoodUnit) -> Unit,
    onQuantitySelected: (Double) -> Unit,
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
                    items = FoodUnit.entries,
                    selectedItem = selectedUnit,
                    onItemSelected = { onUnitSelected(it) },
                    itemLabel = { stringResource(id = it.fullNameResId) })

            }
        }
    }
}