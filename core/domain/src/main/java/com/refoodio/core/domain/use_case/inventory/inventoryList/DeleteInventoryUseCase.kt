package com.refoodio.core.domain.use_case.inventory.inventoryList


import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.inventory.InventoryResource
import com.refoodio.core.domain.util.asResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteInventoryUseCase(private val repository: InventoryRepository) {
    operator fun invoke(inventoryItem: InventoryItem): Flow<InventoryResource<Unit>> = flow {
        emit(repository.deleteInventory(inventoryItem))
    }.asResource()
}