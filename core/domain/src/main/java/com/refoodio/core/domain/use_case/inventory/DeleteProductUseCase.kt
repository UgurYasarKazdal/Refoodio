package com.refoodio.core.domain.use_case.inventory


import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.util.asResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteProductUseCase(private val repository: InventoryRepository) {
    operator fun invoke(product: InventoryItem): Flow<InventoryResource<Unit>> = flow {
        emit(repository.deleteProduct(product))
    }.asResource()
}