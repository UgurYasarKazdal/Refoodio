package com.refoodio.inventory.presentation.add_inventory

import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.model.inventory.InventoryItem

interface InventoryAddContract {
    data class State(
        val suggestions: List<FoodItem> = emptyList(),
        val isLoading: Boolean = false,
        val query: String = "",
        val errorMessage: String? = null

    )

    sealed interface Event {
        data class AddProduct(val product: InventoryItem) : Event
        data class SugesstionsChanged(val query: String) : Event

    }
}