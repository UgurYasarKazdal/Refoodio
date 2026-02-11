package com.refoodio.inventory.presentation.inventory_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.util.Resource
import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.use_case.InventoryUseCases
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
    private val inventoryUseCases: InventoryUseCases
) : ViewModel() {
    // ViewModel içinde:
    private val _effect = Channel<InventoryContract.SideEffect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(InventoryContract.State())
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun handleEvent(event: InventoryContract.Event) {
        when (event) {
            is InventoryContract.Event.AddProduct -> addProduct(event.product)
            is InventoryContract.Event.DeleteProduct -> deleteProduct(event.product)
            is InventoryContract.Event.LoadProducts -> loadProducts()
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            inventoryUseCases.getProducts().collect { list ->
                _state.update { it.copy(products = list) }
            }
        }
    }

    private fun addProduct(product: Product) {
        inventoryUseCases.insertProduct(product).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, errorMessage = null) }
                    // Burada başarılı sinyali (Event) gönderebilirsin
                    // 🚀 İŞTE BURASI: Başarılıysa sinyali gönder
                    _effect.send(InventoryContract.SideEffect.ProductAdded)
                }

                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteProduct(product: Product) {
        viewModelScope.launch {
            inventoryUseCases.deleteProduct(product)
        }
    }
}