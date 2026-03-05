package com.refoodio.core.domain.use_case.inventory.inventoryList

data class InventoryListUseCases(
    val getInventories: GetInventoriesUseCase,
    val deleteInventory: DeleteInventoryUseCase
)