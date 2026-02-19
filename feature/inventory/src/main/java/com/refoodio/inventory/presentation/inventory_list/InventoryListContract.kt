package com.refoodio.inventory.presentation.inventory_list

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.ui.util.UiText

interface InventoryListContract {
    data class State(
        val isLoading: Boolean = false,
        val products: List<InventoryItemUiModel> = emptyList(),
        val errorMessage: String? = null
    )

    data class InventoryItemUiModel(
        val id: Int,
        val name: String,
        val quantityText: UiText,
        val formattedDate: UiText,
        val isCritical: Boolean,
        val originalItem: InventoryItem
    )

    sealed interface Event {
        data object LoadProducts : Event

        data class DeleteProduct(val product: InventoryItem) : Event

        data object NavigateAddInventory : Event
    }

    sealed interface SideEffect {
        /**
         * @param message Gösterilecek metni içeren UiText nesnesi.
         */
        data class ShowSnackbar(val message: UiText) :
            SideEffect

        data object ProductDeleted :
            SideEffect

        data object NavigateToAddInventory : SideEffect
    }
}