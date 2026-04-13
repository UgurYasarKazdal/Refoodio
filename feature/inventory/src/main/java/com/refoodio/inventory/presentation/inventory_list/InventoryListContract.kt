package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.ui.graphics.Color
import com.refoodio.core.domain.model.inventory.FoodGroup
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.ui.util.UiText

interface InventoryListContract {

    enum class UrgencyLevel { NORMAL, WARNING, CRITICAL }

    enum class SortOption(val labelTr: String) {
        EXPIRY_DATE("Son Kullanma"),
        NAME("İsim"),
        QUANTITY("Miktar")
    }

    data class State(
        val isLoading: Boolean = false,
        val criticalItems: List<InventoryItemUiModel> = emptyList(),
        val sectionedItems: Map<FoodGroup, List<InventoryItemUiModel>> = emptyMap(),
        val expandedGroups: Set<FoodGroup> = FoodGroup.values().toSet(),
        // Tezgah seçimi
        val selectedIds: Set<Int> = emptySet(),
        // Toplu silme modu
        val isInBulkDeleteMode: Boolean = false,
        val deleteSelectedIds: Set<Int> = emptySet(),
        val errorMessage: String? = null,
        val searchQuery: String = "",
        val sortOption: SortOption = SortOption.EXPIRY_DATE,
        val consumeItem: InventoryItemUiModel? = null,
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
        data class OnSearchQueryChanged(val query: String) : Event
        data class OnSortOptionChanged(val option: SortOption) : Event
        data class OnConsumeClick(val id: Int) : Event
        data class OnConsumeConfirm(val amount: Double) : Event
        data object OnConsumeDismiss : Event
        data class OnBulkConsume(val amounts: Map<Int, Double>) : Event
        // Swipe to delete (tekil)
        data class OnSwipeDelete(val id: Int) : Event
        data object OnUndoDelete : Event
        // İsraf kaydı
        data object OnMarkSelectedAsWasted : Event
        // Snackbar süresi doldu, geri alınmadı → varsa waste log yaz
        data object OnDeleteConfirmed : Event
        // Toplu silme modu
        data class OnEnterBulkDeleteMode(val id: Int) : Event
        data class OnToggleDeleteSelect(val id: Int) : Event
        data object OnSelectAllForDelete : Event
        data object OnExitBulkDeleteMode : Event
        data object OnConfirmBulkDelete : Event
    }

    sealed interface SideEffect {
        data class ShowSnackbar(val message: UiText) : SideEffect
        data class ShowUndoDeleteSnackbar(val names: String) : SideEffect
        data class NavigateToRecipesWithFilters(val selectedIds: String) : SideEffect
        data object NavigateToAddInventory : SideEffect
        data object NavigateToReceiptScan : SideEffect
        data class NavigateToEditInventory(val itemId: Int) : SideEffect
    }
}
