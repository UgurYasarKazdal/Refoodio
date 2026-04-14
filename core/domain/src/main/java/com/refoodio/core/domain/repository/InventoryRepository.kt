package com.refoodio.core.domain.repository

import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getAllInventories(): Flow<List<InventoryItem>>
    suspend fun addInventory(inventoryItem: InventoryItem): Long
    suspend fun updateInventory(inventoryItem: InventoryItem)
    suspend fun getInventoryById(id: Int): InventoryItem?
    suspend fun deleteInventory(inventoryItem: InventoryItem)
    suspend fun deleteItems(idList: List<Int>)
    suspend fun updateSideUnits(id: Int, sideUnits: List<FoodUnit>)
}