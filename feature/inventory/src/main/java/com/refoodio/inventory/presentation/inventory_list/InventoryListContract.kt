package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.ui.graphics.Color
import com.refoodio.core.domain.model.inventory.FoodGroup
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.ui.util.UiText

interface InventoryListContract {

    enum class UrgencyLevel { NORMAL, WARNING, CRITICAL }

    data class State(
        val isLoading: Boolean = false,
        val criticalItems: List<InventoryItemUiModel> = emptyList(),
        val sectionedItems: Map<FoodGroup, List<InventoryItemUiModel>> = emptyMap(),
        val expandedGroups: Set<FoodGroup> = FoodGroup.values().toSet(),
        val selectedIds: Set<Int> = emptySet(),
        val errorMessage: String? = null,
        val showDeleteConfirmation: Boolean = false,
        // Barkod kamera
        val isCameraVisible: Boolean = false,
        val isBarcodeLoading: Boolean = false,
        val scannedItem: InventoryItem? = null
    )

    data class InventoryItemUiModel(
        val id: Int,
        val name: String,
        val quantityText: UiText,
        val formattedDate: UiText,
        val urgencyLevel: UrgencyLevel,
        val unit: FoodUnit,
        val category: FoodCategory,
        val originalItem: InventoryItem,
        val color: Color,
        val backgroundColor: Color,
        val groupIcon: Int
    ) {
        val isCritical: Boolean get() = urgencyLevel == UrgencyLevel.CRITICAL
    }

    sealed interface Event {
        data object LoadInventories : Event
        data object OnRequestDelete : Event
        data object OnDeleteDismissed : Event
        data object DeleteInventory : Event
        data class OnToggleSelect(val id: Int) : Event
        data object OnFindRecipesClick : Event
        data object OnClearSelection : Event
        data object NavigateAddInventory : Event
        data object NavigateToReceiptScan : Event
        data object OnToggleCamera : Event
        data class OnBarcodeDetected(val barcode: String) : Event
        data class OnScannedItemNameChanged(val name: String) : Event
        data class OnScannedItemQuantityChanged(val quantity: Double) : Event
        data class OnScannedItemUnitChanged(val unit: FoodUnit) : Event
        data class OnScannedItemCategoryChanged(val category: FoodCategory) : Event
        data object OnConfirmBarcodeItem : Event
        data object OnDismissBarcodeItem : Event
        data class ToggleGroupExpansion(val foodGroup: FoodGroup) : Event
        data class OnEditItem(val itemId: Int) : Event
        data class OnDeleteSingleItem(val id: Int) : Event
    }

    sealed interface SideEffect {
        data class ShowSnackbar(val message: UiText) : SideEffect
        data class NavigateToRecipesWithFilters(val selectedIds: String) : SideEffect
        data object NavigateToAddInventory : SideEffect
        data object NavigateToReceiptScan : SideEffect
        data class NavigateToEditInventory(val itemId: Int) : SideEffect
    }
}