package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

@Composable
fun InventoryItem(
    inventoryUiModel: InventoryListContract.InventoryItemUiModel,
    onDeleteClick: (Int) -> Unit,
    onToggleSelect: (Int) -> Unit, // Seçim değişikliği için yeni callback
    isSelected: Boolean, // Seçili olma durumu
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f) // Kare kartlar daha modern durur
            .padding(RefoodioTheme.spacing.small)
            .clickable { onToggleSelect(inventoryUiModel.id) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. SİLME BUTONU (Sağ Üst Köşe)
            IconButton(
                onClick = { onDeleteClick(inventoryUiModel.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.Delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(
                modifier = Modifier.padding(RefoodioTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                /*     // Kategori İkonu (Büyük ve dairesel arka planlı)
                     Box(
                         modifier = Modifier
                             .size(48.dp)
                             .background(
                                 MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                 CircleShape
                             ),
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(
                             painter = painterResource(id = inventoryUiModel.category.imageResourceId),
                             contentDescription = null,
                             modifier = Modifier.size(24.dp),
                             tint = if (inventoryUiModel.isCritical) Color(0xFFE53935) else MaterialTheme.colorScheme.primary
                         )
                     }*/

                Spacer(modifier = Modifier.height(RefoodioTheme.spacing.small))

                Text(
                    text = inventoryUiModel.name,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${inventoryUiModel.quantityText.asString()} ${
                        stringResource(
                            inventoryUiModel.unit.fullNameResId
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (inventoryUiModel.isCritical) {
                    Text(
                        text = "Hemen Tüket!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}