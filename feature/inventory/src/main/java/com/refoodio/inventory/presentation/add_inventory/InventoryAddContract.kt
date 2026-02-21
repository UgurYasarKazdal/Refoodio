package com.refoodio.inventory.presentation.add_inventory

import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.ui.util.UiText

interface InventoryAddContract {
    data class State(
        val isLoading: Boolean = false,
        val isCameraVisible: Boolean = false,
        val searchQuery: String = "",
        val suggestions: List<FoodItem> = emptyList(),
        val errorMessage: String? = null,

        val form: InventoryForm = InventoryForm()
    )

    data class InventoryForm(
        val selectedFoodName: String = "",
        val selectedCategory: String = "",
        val shelfLifeDays: Int = 0,
        val expiryDate: Long? = null,
        val quantity: Double = 1.0,
        val storageNote: String = "",
        val unit: FoodUnit = FoodUnit.KILOGRAM
    )

    sealed interface Event {
        // Arama Olayları
        data class OnQueryChanged(val query: String) : Event
        data class OnSuggestionSelected(val food: FoodItem) : Event

        // Form Olayları
        data object OnIncrementQuantity : Event
        data object OnDecrementQuantity : Event
        data class OnDateChanged(val date: Long) : Event
        data object OnSaveProduct : Event
        data class OnUnitSelected(val unit: FoodUnit) : Event
        data class OnQuantitySelected(val unit: Double) : Event

        // Kamera ve İzin Olayları
        data object OnToggleCamera : Event
        data class OnBarcodeScanned(val barcode: String) : Event
        data object OnPermissionDenied : Event
    }

    sealed class SideEffect {
        object NavigateBack : SideEffect()
        data class ShowSnackBar(val message: UiText) : SideEffect()
    }
}