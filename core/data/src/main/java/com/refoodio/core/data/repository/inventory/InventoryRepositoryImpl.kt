package com.refoodio.core.data.repository.inventory

import com.refoodio.core.data.mapper.inventory.toDomain
import com.refoodio.core.data.mapper.inventory.toEntity
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryDao: InventoryDao
) : InventoryRepository {

    override fun getAllProducts(): Flow<List<InventoryItem>> {
        // Database'den Flow<List<ProductEntity>> gelir
        return inventoryDao.getProductsFlow().map { entities ->
            // Her bir Entity'yi Domain modeline (Product) çeviriyoruz
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addProduct(product: InventoryItem) {
        // Domain modelini kaydedebilmek için Entity'ye çeviriyoruz
        inventoryDao.insertProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: InventoryItem) {
        inventoryDao.deleteProduct(product.toEntity())
    }
}