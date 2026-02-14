package com.refoodio.inventory.presentation.inventory_list

import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.ui.util.UiText

interface InventoryContract {

    // Ekranın o anki "fotoğrafı" (State)
    data class State(
        val isLoading: Boolean = false,
        val products: List<InventoryItem> = emptyList(),
        val errorMessage: String? = null,
        val isAddProductDialogOpen: Boolean = false
    )

    // Kullanıcının yapabileceği hareketler (Events / Intents)
    sealed interface Event {
        data object LoadProducts : Event
        data class AddProduct(val product: InventoryItem) : Event
        data class DeleteProduct(val product: InventoryItem) : Event
        data object ShowAddProductDialog : Event

        data object DismissAddProductDialog : Event

    }

    sealed interface SideEffect {
        /**
         * UI'a bir Snackbar göstermesini söyler.
         * @param message Gösterilecek metni içeren UiText nesnesi.
         */
        data class ShowSnackbar(val message: UiText) : SideEffect

        /**
         * Ürün eklendikten sonra yapılacak bir eylem (örneğin bir sonraki ekrana geçiş).
         */
        data object ProductAdded : SideEffect
        /**
         * Ürün silindikten sonra yapılacak bir eylem (şimdilik boş).
         */
        data object ProductDeleted : SideEffect    }
}