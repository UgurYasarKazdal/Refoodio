package com.refoodio.inventory.presentation.receipt_scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.inventory.addInventory.ScanReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptScanViewModel @Inject constructor(
    private val scanReceiptUseCase: ScanReceiptUseCase,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReceiptScanContract.State())
    val state = _state.asStateFlow()

    private val _effect = Channel<ReceiptScanContract.SideEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleEvent(event: ReceiptScanContract.Event) {
        when (event) {
            is ReceiptScanContract.Event.OnPhotoCaptured -> scanReceipt(event.imageBytes)

            is ReceiptScanContract.Event.OnItemNameChanged -> updateItem(event.index) {
                it.copy(name = event.name)
            }

            is ReceiptScanContract.Event.OnItemQuantityChanged -> updateItem(event.index) {
                it.copy(quantity = event.quantity)
            }

            is ReceiptScanContract.Event.OnItemUnitChanged -> updateItem(event.index) {
                it.copy(unit = event.unit)
            }

            is ReceiptScanContract.Event.OnItemCategoryChanged -> updateItem(event.index) {
                it.copy(category = event.category)
            }

            is ReceiptScanContract.Event.OnItemRemoved -> {
                _state.update { s ->
                    s.copy(scannedItems = s.scannedItems.toMutableList().also { it.removeAt(event.index) })
                }
            }

            ReceiptScanContract.Event.OnConfirmItems -> saveItems()

            ReceiptScanContract.Event.OnRetry -> {
                _state.update { it.copy(phase = ReceiptScanContract.Phase.SCANNING, errorMessage = null) }
            }

            ReceiptScanContract.Event.OnNavigateBack -> {
                viewModelScope.launch { _effect.send(ReceiptScanContract.SideEffect.NavigateBack) }
            }
        }
    }

    private fun scanReceipt(imageBytes: ByteArray) {
        _state.update { it.copy(phase = ReceiptScanContract.Phase.LOADING, errorMessage = null) }
        viewModelScope.launch {
            scanReceiptUseCase(imageBytes)
                .onSuccess { items ->
                    _state.update {
                        it.copy(
                            phase = ReceiptScanContract.Phase.REVIEWING,
                            scannedItems = items
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            phase = ReceiptScanContract.Phase.SCANNING,
                            errorMessage = e.message ?: "Fiş okunamadı"
                        )
                    }
                    _effect.send(ReceiptScanContract.SideEffect.ShowSnackbar(e.message ?: "Fiş okunamadı"))
                }
        }
    }

    private fun saveItems() {
        viewModelScope.launch {
            _state.value.scannedItems.forEach { item ->
                inventoryRepository.addInventory(item)
            }
            _effect.send(ReceiptScanContract.SideEffect.NavigateBack)
        }
    }

    private fun updateItem(index: Int, transform: (InventoryItem) -> InventoryItem) {
        _state.update { s ->
            val updated = s.scannedItems.toMutableList()
            if (index in updated.indices) updated[index] = transform(updated[index])
            s.copy(scannedItems = updated)
        }
    }
}
