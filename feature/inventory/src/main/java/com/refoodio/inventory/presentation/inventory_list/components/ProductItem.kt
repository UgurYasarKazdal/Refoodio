package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

@Composable
fun ProductItem(
    inventoryUiModel: InventoryListContract.InventoryItemUiModel,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(RefoodioTheme.spacing.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = RefoodioTheme.spacing.small)
    ) {
        Row(
            modifier = Modifier
                .padding(RefoodioTheme.spacing.large)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = inventoryUiModel.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = inventoryUiModel.quantityText.asString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = inventoryUiModel.formattedDate.asString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (inventoryUiModel.isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.Delete),
                    tint = Color.Red
                )
            }
        }
    }
}