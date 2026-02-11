package com.refoodio.inventory.domain.repository

import com.refoodio.inventory.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getAllProducts(): Flow<List<Product>> // Domain modeli döndürüyoruz!
    suspend fun addProduct(product: Product)

    suspend fun deleteProduct(product: Product)
}