package com.refoodio.inventory.domain.use_case

data class InventoryUseCases(
    val getProducts: GetProductsUseCase,
    val insertProduct: InsertProductUseCase,
    val deleteProduct: DeleteProductUseCase
)