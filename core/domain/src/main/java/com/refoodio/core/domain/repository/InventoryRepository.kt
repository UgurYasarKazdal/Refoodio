package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.inventory.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getAllProducts(): Flow<List<InventoryItem>>
    suspend fun addProduct(product: InventoryItem)

    suspend fun deleteProduct(product: InventoryItem)
}