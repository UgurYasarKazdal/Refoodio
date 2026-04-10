package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.ui.graphics.Color
import com.refoodio.core.domain.model.inventory.FoodGroup
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.ui.util.UiText

interface InventoryListContract {
    data class State(
        val isLoading: Boolean = false,
        val criticalItems: List<InventoryItemUiModel> = emptyList(),
        val sectionedItems: Map<FoodGroup, List<InventoryItemUiModel>> = emptyMap(),
        val expandedGroups: Set<FoodGroup> = FoodGroup.values().toSet(),
        val selectedIds: Set<Int> = emptySet(),
        val errorMessage: String? = null,
        // Barkod kamera
        val isCameraVisible: Boolean = false,
        val isBarcodeLoading: Boolean = false,
        val scannedItem: InventoryItem? = null  // barkoddan gelen ürün onay dialogu için
    )

    data class InventoryItemUiModel(
        val id: Int,
        val name: String,
        val quantityText: UiText,
        val formattedDate: UiText,
        val isCritical: Boolean,
        val unit: FoodUnit,
        val category: FoodCategory,
        val originalItem: InventoryItem,
        val color: Color, // ID yerine doğrudan Compose Color nesnesi
        val backgroundColor: Color,
        val groupIcon: Int // cop
    )

    sealed interface Event {
        data object LoadInventories : Event

        // Silme işlemi için ID yeterli olacaktır
        data object DeleteInventory : Event

        // Kart seçimi/iptali için yeni event
        data class OnToggleSelect(val id: Int) : Event

        // Sihirbaz barındaki "Tarif Bul" butonu
        data object OnFindRecipesClick : Event

        // Tüm seçimleri temizle
        data object OnClearSelection : Event

        data object NavigateAddInventory : Event
        data object NavigateToReceiptScan : Event
        data object OnToggleCamera : Event
        data class OnBarcodeDetected(val barcode: String) : Event
        data object OnConfirmBarcodeItem : Event
        data object OnDismissBarcodeItem : Event

        data class ToggleGroupExpansion(val foodGroup: FoodGroup) : Event
    }

    sealed interface SideEffect {
        data class ShowSnackbar(val message: UiText) : SideEffect
        data class NavigateToRecipesWithFilters(val selectedIds: String) : SideEffect
        data object NavigateToAddInventory : SideEffect
        data object NavigateToReceiptScan : SideEffect
    }
}