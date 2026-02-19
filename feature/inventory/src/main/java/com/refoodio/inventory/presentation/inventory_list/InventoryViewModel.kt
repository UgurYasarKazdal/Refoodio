package com.refoodio.inventory.presentation.inventory_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.use_case.inventory.inventoryList.InventoryListUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.toReadableDate
import com.refoodio.core.ui.util.UiText
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.util.asInventoryErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryListUseCases: InventoryListUseCases,
    private val app: Application
) : ViewModel() {
    private val _effect = Channel<InventoryListContract.SideEffect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(InventoryListContract.State())
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun handleEvent(event: InventoryListContract.Event) {
        when (event) {
            is InventoryListContract.Event.DeleteProduct -> deleteProduct(event.product)
            is InventoryListContract.Event.LoadProducts -> loadProducts()
            is InventoryListContract.Event.NavigateAddInventory -> {
                viewModelScope.launch {
                    _effect.send(InventoryListContract.SideEffect.NavigateToAddInventory)
                }
            }
        }
    }

    private fun loadProducts() {
        inventoryListUseCases.getProducts().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    val uiProducts = result.data.map { item ->
                        InventoryListContract.InventoryItemUiModel(
                            id = item.id,
                            name = item.name,
                            quantityText = UiText.StringResource(R.string.quantity, item.quantity),
                            formattedDate = UiText.StringResource(
                                R.string.add_inventory_expiry_date, item.expiryDate.toReadableDate()
                            ),
                            isCritical = item.isNearExpiry(),
                            originalItem = item
                        )
                    }

                    _state.update {
                        it.copy(
                            isLoading = false, products = uiProducts
                        )
                    }
                }

                is Resource.Error -> {
                    val uiText = result.errorType.asInventoryErrorText()
                    _state.update {
                        it.copy(isLoading = false, errorMessage = uiText.asString(app))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun deleteProduct(product: InventoryItem) {
        inventoryListUseCases.deleteProduct(product).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(
                        InventoryListContract.SideEffect.ShowSnackbar(
                            UiText.StringResource(R.string.product_deleted_successfully)
                        )
                    )
                }

                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    val uiText = result.errorType.asInventoryErrorText()
                    _effect.send(InventoryListContract.SideEffect.ShowSnackbar(uiText))
                }
            }
        }.launchIn(viewModelScope)
    }
}