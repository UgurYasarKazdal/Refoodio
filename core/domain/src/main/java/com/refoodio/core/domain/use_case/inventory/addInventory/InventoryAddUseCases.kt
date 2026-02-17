package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.use_case.catalog.GetFoodSuggestionsUseCase

data class InventoryAddUseCases(
    val insertProduct: InsertProductUseCase,
    val suggestionsUseCase: GetFoodSuggestionsUseCase,
    val getFoodByBarcodeUseCase: GetFoodByBarcodeUseCase
)