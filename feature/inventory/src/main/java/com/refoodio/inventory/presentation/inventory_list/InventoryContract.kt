package com.refoodio.inventory.presentation.inventory_list

import com.refoodio.core.util.UiText
import com.refoodio.inventory.domain.model.Product

interface InventoryContract {

    // Ekranın o anki "fotoğrafı" (State)
    data class State(
        val isLoading: Boolean = false,
        val products: List<Product> = emptyList(),
        val errorMessage: String? = null,
        val isAddProductDialogOpen: Boolean = false
    )

    // Kullanıcının yapabileceği hareketler (Events / Intents)
    sealed interface Event {
        data object LoadProducts : Event
        data class AddProduct(val product: Product) : Event
        data class DeleteProduct(val product: Product) : Event
        data object ShowAddProductDialog : Event

        data object DismissAddProductDialog : Event

    }

    sealed class SideEffect {
        /**
         * UI'a bir Snackbar göstermesini söyler.
         * @param message Gösterilecek metni içeren UiText nesnesi.
         */
        data class ShowSnackbar(val message: UiText) : SideEffect()

        /**
         * Ürün eklendikten sonra yapılacak bir eylem (örneğin bir sonraki ekrana geçiş).
         */
        object ProductAdded : SideEffect()

        /**
         * Ürün silindikten sonra yapılacak bir eylem (şimdilik boş).
         */
        object ProductDeleted : SideEffect()
    }
}