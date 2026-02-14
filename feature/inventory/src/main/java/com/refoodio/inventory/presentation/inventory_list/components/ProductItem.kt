package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.* // Tüm material3 bileşenlerini (Icon, Card vs.) buradan alıyoruz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Doğru renk kütüphanesi
import androidx.compose.ui.unit.dp
import com.refoodio.core.domain.model.inventory.InventoryItem

@Composable
fun ProductItem(
    product: InventoryItem,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) { // Yazıların butonu sıkıştırmaması için weight ekledik
                Text(text = product.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "Miktar: ${product.quantity}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "SKT: ${product.expiryDate}", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDeleteClick) {
                // Doğru Icon kullanımı:
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.Red // Küçük 'ed' ile Red
                )
            }
        }
    }
}