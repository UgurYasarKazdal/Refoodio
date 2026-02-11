package com.refoodio.inventory.presentation.inventory_list

import com.refoodio.inventory.domain.model.Product

interface InventoryContract {

    // Ekranın o anki "fotoğrafı" (State)
    data class State(
        val isLoading: Boolean = false,
        val products: List<Product> = emptyList(),
        val error: String? = null
    )

    // Kullanıcının yapabileceği hareketler (Events / Intents)
    sealed interface Event {
        data object LoadProducts : Event
        data class AddProduct(val product: Product) : Event
        data class DeleteProduct(val product: Product) : Event
    }
}