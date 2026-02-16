package com.refoodio.inventory.presentation.add_inventory

import com.refoodio.core.domain.model.catalog.FoodItem

interface InventoryAddContract {
    data class State(
        val suggestions: List<FoodItem> = emptyList(),
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val errorMessage: String? = null,
        val selectedFoodName: String = "",
        val shelfLifeDays: Int = 0,
        val expiryDate: Long? = null, // Hesaplanan milisaniye cinsinden tarih
        val quantity: Int = 1,        // Varsayılan miktar
        val selectedCategory: String = "",
        val storageNote: String = ""

    )

    sealed class Event {
        data class OnQueryChanged(val query: String) : Event()
        data class OnSuggestionSelected(val food: FoodItem) : Event()
        object OnIncrementQuantity : Event()
        object OnDecrementQuantity : Event()
        data class OnDateChanged(val date: Long) : Event()
        object OnSaveProduct : Event()

    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}