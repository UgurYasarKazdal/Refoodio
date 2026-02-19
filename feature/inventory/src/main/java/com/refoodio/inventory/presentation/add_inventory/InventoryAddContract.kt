package com.refoodio.inventory.presentation.add_inventory

import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.ui.util.UiText

interface InventoryAddContract {
    data class State(
        val suggestions: List<FoodItem> = emptyList(),
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val errorMessage: String? = null,
        val selectedFoodName: String = "",
        val shelfLifeDays: Int = 0,
        val expiryDate: Long? = null,
        val quantity: Int = 1,
        val selectedCategory: String = "",
        val storageNote: String = "",
        val isCameraVisible: Boolean = false

    )

    sealed class Event {
        data class OnQueryChanged(val query: String) : Event()
        data class OnSuggestionSelected(val food: FoodItem) : Event()
        object OnIncrementQuantity : Event()
        object OnDecrementQuantity : Event()
        data class OnDateChanged(val date: Long) : Event()
        object OnSaveProduct : Event()

        object OnPermissionDenied : Event() // Yeni Event
        data class OnBarcodeScanned(val barcode: String) : Event()
        object OnToggleCamera : Event() // Kamerayı aç/kapat

    }

    sealed class SideEffect {
        object NavigateBack : SideEffect()
        data class ShowSnackBar(val message: UiText) : SideEffect()
    }
}