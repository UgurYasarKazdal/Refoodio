package com.refoodio.core.domain.use_case.inventory.inventoryList

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.inventory.InventoryResource
import com.refoodio.core.domain.util.asResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class GetProductsUseCase(private val repository: InventoryRepository) {
    operator fun invoke(): Flow<InventoryResource<List<InventoryItem>>> {
        return repository.getAllProducts()
            .map { it.sortedBy { item -> item.expiryDate } }
            .distinctUntilChanged()
            .asResource()
    }
}