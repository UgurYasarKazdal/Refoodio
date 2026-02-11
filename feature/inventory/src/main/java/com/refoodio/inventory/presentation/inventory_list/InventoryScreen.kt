package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.inventory.domain.model.Product
import kotlin.random.Random

import com.refoodio.inventory.presentation.inventory_list.components.ProductItem

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Test için ürün ekleme
                viewModel.handleEvent(InventoryContract.Event.AddProduct(
                    Product(name = viewModel.mockProducts.random(),
                        expiryDate = System.currentTimeMillis(),
                        quantity = Random.nextDouble(1.0, 10.0)
                )))
            }) { Icon(Icons.Default.Add, contentDescription = "Ekle") }
        }
    ) { padding ->
        if (state.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Mutfak boş, haydi alışverişe! 🛒")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Kartlar arası boşluk
            ) {
                items(
                    items = state.products,
                    key = { it.id ?: it.hashCode() } // Performans ve animasyon için önemli
                ) { product ->
                    // İŞTE BURADA KULLANIYORUZ:
                    ProductItem(
                        product = product,
                        onDeleteClick = {
                            viewModel.handleEvent(InventoryContract.Event.DeleteProduct(product))
                        }
                    )
                }
            }
        }
    }
}