package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.use_case.catalog.GetFoodSuggestionsUseCase

data class InventoryAddUseCases(
    val insertInventory: InsertInventoryUseCase,
    val suggestionsUseCase: GetFoodSuggestionsUseCase,
    val getFoodByBarcodeUseCase: GetFoodByBarcodeUseCase
)