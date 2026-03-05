package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.inventory.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getAllInventories(): Flow<List<InventoryItem>>
    suspend fun addInventory(inventoryItem: InventoryItem)

    suspend fun deleteInventory(inventoryItem: InventoryItem)
}