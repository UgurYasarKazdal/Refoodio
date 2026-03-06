package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.refoodio.core.domain.model.inventory.toFoodGroup
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract


@Composable
fun CriticalCarouselItem(
    item: InventoryListContract.InventoryItemUiModel,
    isSelected: Boolean,
    onToggleSelect: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(160.dp) // Sabit genişlik carousel için iyidir
            .height(100.dp)
            .clickable { onToggleSelect(item.id) },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.errorContainer 
                else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp, 
            color = if (isSelected) MaterialTheme.colorScheme.error 
                    else MaterialTheme.colorScheme.outlineVariant
        ),
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            // Silme Butonu (Mini)
            IconButton(
                onClick = { onDeleteClick(item.id) },
                modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close, // Carousel'de X daha şık durabilir
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = item.category.toFoodGroup().iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.formattedDate.asString(), // "Bitiş: 07 Mart"
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}