package com.refoodio.inventory

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeInventoryRepository : InventoryRepository {
    private val products = mutableListOf<InventoryItem>()
    private val productsFlow = MutableStateFlow<List<InventoryItem>>(emptyList())

    override fun getAllInventories(): Flow<List<InventoryItem>> {
        return productsFlow
    }

    override suspend fun addInventory(product: InventoryItem) {
        products.add(product)
        productsFlow.emit(ArrayList(products)) // Liste kopyasını gönderiyoruz
    }

    override suspend fun deleteInventory(product: InventoryItem) {
        products.remove(product)
        productsFlow.emit(ArrayList(products))
    }

}