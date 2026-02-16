package com.refoodio.core.domain.use_case.inventory.inventoryList

data class InventoryListUseCases(
    val getProducts: GetProductsUseCase,
    val deleteProduct: DeleteProductUseCase
)