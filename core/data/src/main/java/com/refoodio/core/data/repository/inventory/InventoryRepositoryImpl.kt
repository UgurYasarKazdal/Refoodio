package com.refoodio.core.data.repository.inventory

import com.refoodio.core.data.mapper.inventory.toDomain
import com.refoodio.core.data.mapper.inventory.toEntity
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryDao: InventoryDao
) : InventoryRepository {

    override fun getAllInventories(): Flow<List<InventoryItem>> {
        return inventoryDao.getInventoriesFlow().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun addInventory(inventoryItem: InventoryItem) {
        withContext(Dispatchers.IO) {
            inventoryDao.insertInventory(inventoryItem.toEntity())
        }
    }

    override suspend fun deleteInventory(inventoryItem: InventoryItem) {
        withContext(Dispatchers.IO) {
            inventoryDao.deleteInventory(inventoryItem.toEntity())
        }
    }

    override suspend fun deleteItems(idList: List<Int>) {
        withContext(Dispatchers.IO) {
            inventoryDao.deleteItemsByIds(idList)
        }
    }
}