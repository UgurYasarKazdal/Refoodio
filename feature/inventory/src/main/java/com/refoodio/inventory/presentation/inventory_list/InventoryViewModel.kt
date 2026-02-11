package com.refoodio.inventory.presentation.inventory_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.inventory.domain.model.Product
import com.refoodio.inventory.domain.use_case.DeleteProductUseCase
import com.refoodio.inventory.domain.use_case.GetProductsUseCase
import com.refoodio.inventory.domain.use_case.InsertProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val insertProductUseCase: InsertProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryContract.State())
    val state = _state.asStateFlow()

    val mockProducts =listOf("Elma", "Armut", "Muz", "Çilek", "Kiraz","Peynir","Sebze")

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
            getProductsUseCase().collect { list ->
                _state.update { it.copy(products = list) }
            }
        }
    }

    private fun addProduct(product: Product) {
        viewModelScope.launch {
            insertProductUseCase(product)
        }
    }

    private fun deleteProduct(product: Product) {
        viewModelScope.launch {
            deleteProductUseCase(product)
        }
    }
}