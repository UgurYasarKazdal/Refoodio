package com.refoodio.inventory.presentation.inventory_list

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.ui.util.UiText

interface InventoryListContract {

    // Ekranın o anki "fotoğrafı" (State)
    data class State(
        val isLoading: Boolean = false,
        val products: List<InventoryItem> = emptyList(),
        val errorMessage: String? = null
    )

    // Kullanıcının yapabileceği hareketler (Events / Intents)
    sealed interface Event {
        data object LoadProducts : Event

        data class DeleteProduct(val product: InventoryItem) : Event

        data object NavigateAddInventory : Event
    }

    sealed interface SideEffect {
        /**
         * UI'a bir Snackbar göstermesini söyler.
         * @param message Gösterilecek metni içeren UiText nesnesi.
         */
        data class ShowSnackbar(val message: UiText) :
            SideEffect

        /**
         * Ürün silindikten sonra yapılacak bir eylem (şimdilik boş).
         */
        data object ProductDeleted :
            SideEffect
    }
}