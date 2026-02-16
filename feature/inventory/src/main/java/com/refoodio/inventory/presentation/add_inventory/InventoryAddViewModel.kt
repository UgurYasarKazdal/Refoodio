package com.refoodio.inventory.presentation.add_inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.daysToMillis
import com.refoodio.inventory.presentation.util.asInventoryErrorText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class InventoryAddViewModel @Inject constructor(private val inventoryAddUseCases: InventoryAddUseCases) :
    ViewModel() {
    private val _query = MutableStateFlow("")

    private val _state = MutableStateFlow(InventoryAddContract.State())

    // 2. Arama akışını dinleyip ana state'i güncelleyen bir yapı kur
    init {
        observeQuery()
    }

    private fun observeQuery() {
        _query.debounce(300)
            .filter { it.length >= 2 }
            .flatMapLatest { query ->
                // Arama başladığında loading yapabilirsin
                inventoryAddUseCases.suggestionsUseCase(query)
            }
            .onEach { results ->
                // Gelen sonuçları ana state'e yaz
                _state.update { it.copy(suggestions = results) }
            }
            .launchIn(viewModelScope)
    }

    val state: StateFlow<InventoryAddContract.State> = _state.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun addProduct() {
        val currentState = _state.value
        if (currentState.isLoading) return
        if (currentState.selectedFoodName.isBlank()) return
        val newItem = InventoryItem(
            name = currentState.selectedFoodName,
            expiryDate = currentState.expiryDate ?: System.currentTimeMillis(),
            quantity = currentState.quantity.toDouble() // State'deki Int'i Double'a çeviriyoruz
        )
        inventoryAddUseCases.insertProduct(newItem).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedFoodName = "", // Formu temizle
                            suggestions = emptyList(),
                            errorMessage = null
                        )
                    }                    // Burada başarılı sinyali (Event) gönderebilirsin
                    // 🚀 İŞTE BURASI: Başarılıysa sinyali gönder
                }

                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    // 🎯 YENİ YAPI: SideEffect olarak Snackbar'a gönder
                    val uiText = result.errorType.asInventoryErrorText()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onSuggestionSelected(foodItem: FoodItem) {
        // Akıllı Tarih Hesaplama: Bugün + Raf Ömrü
        val calculatedExpiry = System.currentTimeMillis() + foodItem.defaultShelfLife.daysToMillis

        _state.update {
            it.copy(
                selectedFoodName = foodItem.name,
                shelfLifeDays = foodItem.defaultShelfLife,
                expiryDate = calculatedExpiry,
                selectedCategory = foodItem.category,
                quantity = 1, // Her yeni seçimde miktarı resetle
                suggestions = emptyList() // Yarış durumunu engellemek için listeyi temizle
            )
        }
    }

    fun onIncrementQuantity() {
        _state.update { it.copy(quantity = it.quantity + 1) }
    }

    fun onDecrementQuantity() {
        _state.update {
            val newQuantity = if (it.quantity > 1) it.quantity - 1 else 1
            it.copy(quantity = newQuantity)
        }
    }
}