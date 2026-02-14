package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.inventory.presentation.inventory_list.InventoryContract

@Composable
fun AddProductDialog(
    state: InventoryContract.State,
    onEvent: (InventoryContract.Event) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Ürün Ekle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Ürün İsmi
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ürün Adı") },
                    isError = state.errorMessage?.contains("ad") == true // TDD ile gelen hata kontrolü
                )

                // Miktar
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Miktar") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.errorMessage?.contains("Miktar") == true // TDD ile gelen hata kontrolü
                )

                // Hata Mesajı (Eğer varsa)
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val product = InventoryItem(
                    name = name,
                    quantity = quantity.toDoubleOrNull()
                        ?: -1.0, // Geçersiz miktar testi burada devreye girecek
                    expiryDate = System.currentTimeMillis()
                )
                onEvent(InventoryContract.Event.AddProduct(product))
            }) {
                Text("Ekle")
            }
        }
    )
}