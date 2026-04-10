package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.inventory.InventoryResource
import com.refoodio.core.domain.util.CommonError
import com.refoodio.core.domain.util.ValidationException
import com.refoodio.core.domain.util.asResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateInventoryUseCase(
    private val repository: InventoryRepository,
    private val validateInventory: ValidateInventoryUseCase
) {
    operator fun invoke(inventoryItem: InventoryItem): Flow<InventoryResource<Unit>> = flow {
        val validation = validateInventory.execute(inventoryItem)

        if (!validation.successful) {
            throw ValidationException(errorType = validation.errorType ?: CommonError.UNKNOWN_ERROR)
        }

        emit(repository.updateInventory(inventoryItem))

    }.asResource()
}
