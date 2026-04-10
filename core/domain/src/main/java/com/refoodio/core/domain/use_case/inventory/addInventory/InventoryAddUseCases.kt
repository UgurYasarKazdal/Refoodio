package com.refoodio.core.domain.use_case.inventory.addInventory

import com.refoodio.core.domain.use_case.catalog.GetFoodSuggestionsUseCase

import com.refoodio.core.domain.model.inventory.InventoryItem

data class InventoryAddUseCases(
    val insertInventory: InsertInventoryUseCase,
    val updateInventory: UpdateInventoryUseCase,
    val getInventoryById: suspend (Int) -> InventoryItem?,
    val suggestionsUseCase: GetFoodSuggestionsUseCase,
    val getFoodByBarcodeUseCase: GetFoodByBarcodeUseCase
)