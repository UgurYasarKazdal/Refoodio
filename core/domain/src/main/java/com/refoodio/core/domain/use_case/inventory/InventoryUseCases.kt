package com.refoodio.core.domain.use_case.inventory

data class InventoryUseCases(
    val getProducts: GetProductsUseCase,
    val insertProduct: InsertProductUseCase,
    val deleteProduct: DeleteProductUseCase
)