package com.refoodio.inventory.domain.repository

import com.refoodio.inventory.domain.repository.InventoryRepository
import com.refoodio.inventory.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeInventoryRepository : InventoryRepository {

    // Gerçek veritabanı yerine bellekte (RAM) tutuyoruz
    private val products = mutableListOf<Product>()
    private val productsFlow = MutableStateFlow<List<Product>>(emptyList())

    override fun getAllProducts(): Flow<List<Product>> {
        return productsFlow
    }

    override suspend fun addProduct(product: Product) {
        products.add(product)
        productsFlow.emit(ArrayList(products)) // Liste kopyasını gönderiyoruz
    }

    override suspend fun deleteProduct(product: Product) {
        products.remove(product)
        productsFlow.emit(ArrayList(products))
    }

}