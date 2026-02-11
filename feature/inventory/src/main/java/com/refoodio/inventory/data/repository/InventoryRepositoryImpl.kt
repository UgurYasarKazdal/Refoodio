package com.refoodio.inventory.data.repository

import com.refoodio.inventory.data.local.ProductDao
import com.refoodio.inventory.data.mapper.toDomain
import com.refoodio.inventory.data.mapper.toEntity
import com.refoodio.inventory.domain.repository.InventoryRepository
import com.refoodio.inventory.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// feature:inventory/data/repository/InventoryRepositoryImpl.kt

class InventoryRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : InventoryRepository {

    override fun getAllProducts(): Flow<List<Product>> {
        // Database'den Flow<List<ProductEntity>> gelir
        return productDao.getProductsFlow().map { entities ->
            // Her bir Entity'yi Domain modeline (Product) çeviriyoruz
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addProduct(product: Product) {
        // Domain modelini kaydedebilmek için Entity'ye çeviriyoruz
        productDao.insertProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }


}