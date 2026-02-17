package com.refoodio.inventory.presentation.add_inventory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.daysToMillis
import com.refoodio.inventory.presentation.util.asInventoryErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryAddViewModel @Inject constructor(private val inventoryAddUseCases: InventoryAddUseCases) :
    ViewModel() {

    private val _effect = Channel<InventoryAddContract.Effect.NavigateBack>()
    val effect = _effect.receiveAsFlow()

    private val searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(InventoryAddContract.State())

    // 2. Arama akışını dinleyip ana state'i güncelleyen bir yapı kur
    init {
        observeQuery()
    }

    private fun observeQuery() {
        searchQuery.debounce(300)
            .filter { it.length >= 2 }
            .flatMapLatest { query ->
                // Arama başladığında loading yapabilirsin
                inventoryAddUseCases.suggestionsUseCase("$query*")
            }
            .onEach { results ->
                // Gelen sonuçları ana state'e yaz
                _state.update { it.copy(suggestions = results) }
            }
            .launchIn(viewModelScope)
    }

    val state: StateFlow<InventoryAddContract.State> = _state.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _state.update { it.copy(searchQuery = newQuery) }
        searchQuery.value = newQuery
    }


    fun handleEvent(event: InventoryAddContract.Event) {
        when (event) {
            is InventoryAddContract.Event.OnQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchQuery.value = event.query
            }

            is InventoryAddContract.Event.OnSuggestionSelected -> {
                // Akıllı Tarih Hesaplama: Bugün + Raf Ömrü
                val calculatedExpiry =
                    System.currentTimeMillis() + event.food.defaultShelfLife.daysToMillis

                _state.update {
                    it.copy(
                        selectedFoodName = event.food.name,
                        shelfLifeDays = event.food.defaultShelfLife,
                        expiryDate = calculatedExpiry,
                        selectedCategory = event.food.category,
                        quantity = 1, // Her yeni seçimde miktarı resetle
                        suggestions = emptyList() // Yarış durumunu engellemek için listeyi temizle
                    )
                }
            }

            InventoryAddContract.Event.OnIncrementQuantity -> {
                _state.update { it.copy(quantity = it.quantity + 1) }
            }

            InventoryAddContract.Event.OnDecrementQuantity -> {
                if (_state.value.quantity > 1) {
                    _state.update { it.copy(quantity = it.quantity - 1) }
                }
            }

            is InventoryAddContract.Event.OnDateChanged -> {
                _state.update { it.copy(expiryDate = event.date) }
            }

            InventoryAddContract.Event.OnSaveProduct -> {
                saveProduct()
            }

            is InventoryAddContract.Event.OnBarcodeScanned -> {
                if (state.value.isLoading) return

                _state.update { it.copy(isLoading = true) }
                Log.d("BarcodeScanned", "Barkod Yakalandı: ${event.barcode}")

                // Simülasyon: 2 saniye sonra kamerayı kapat ve loading'i bitir
                viewModelScope.launch {
                    delay(2000)
                    _state.update { it.copy(isCameraVisible = false, isLoading = false) }
                    // Burada event.barcode'u UI'da bir TextField'a set edebilirsin
                }
                //TODO: https://tr.openfoodfacts.org/api/v2/product/8692886310711.json kullanılacak. network retrofit
                // searchProductByBarcode(event.barcode) // Barkoda göre ürünü getir
            }

            is InventoryAddContract.Event.OnToggleCamera -> {
                _state.update { it.copy(isCameraVisible = !it.isCameraVisible) }
            }
        }
    }

    fun saveProduct() {
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
                    }

                    _effect.send(InventoryAddContract.Effect.NavigateBack)// Burada başarılı sinyali (Event) gönderebilirsin
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
}