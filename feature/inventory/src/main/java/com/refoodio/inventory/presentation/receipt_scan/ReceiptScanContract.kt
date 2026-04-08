package com.refoodio.inventory.presentation.receipt_scan

import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory

interface ReceiptScanContract {

    enum class Phase { SCANNING, LOADING, REVIEWING }

    data class State(
        val phase: Phase = Phase.SCANNING,
        val scannedItems: List<InventoryItem> = emptyList(),
        val errorMessage: String? = null
    )

    sealed interface Event {
        data class OnPhotoCaptured(val imageBytes: ByteArray) : Event {
            override fun equals(other: Any?) = other is OnPhotoCaptured && imageBytes.contentEquals(other.imageBytes)
            override fun hashCode() = imageBytes.contentHashCode()
        }

        data class OnItemNameChanged(val index: Int, val name: String) : Event
        data class OnItemQuantityChanged(val index: Int, val quantity: Double) : Event
        data class OnItemUnitChanged(val index: Int, val unit: FoodUnit) : Event
        data class OnItemCategoryChanged(val index: Int, val category: FoodCategory) : Event
        data class OnItemRemoved(val index: Int) : Event
        data object OnConfirmItems : Event
        data object OnRetry : Event
        data object OnNavigateBack : Event
    }

    sealed class SideEffect {
        data object NavigateBack : SideEffect()
        data class ShowSnackbar(val message: String) : SideEffect()
    }
}
