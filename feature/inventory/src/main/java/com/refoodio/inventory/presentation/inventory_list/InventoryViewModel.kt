package com.refoodio.inventory.presentation.inventory_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.use_case.inventory.inventoryList.InventoryListUseCases
import com.refoodio.core.domain.util.Resource
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
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryListUseCases: InventoryListUseCases,
    private val app: Application // Hilt ile Application'ı enjekte edin
) : ViewModel() {
    // ViewModel içinde:
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
            is InventoryListContract.Event.NavigateAddInventory -> {//TODO: Navigate Add Inventory
            }
        }
    }

    // --- BİTİRİLMİŞ `loadProducts` FONKSİYONU ---
    private fun loadProducts() {
        inventoryListUseCases.getProducts().onEach { result ->
            // UseCase'den gelen Resource akışını işle
            when (result) {
                is Resource.Loading -> {
                    // Veri henüz gelmediğinde yükleme durumunu ayarla
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    // Başarılı veri geldiğinde listeyi ve yükleme durumunu güncelle
                    _state.update {
                        it.copy(
                            isLoading = false,
                            products = result.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    // 🎯 YENİ YAPI: Enum'ı al, UiText'e çevir, String yap ve State'e bas
                    val uiText = result.errorType.asInventoryErrorText()
                    _state.update {
                        it.copy(isLoading = false, errorMessage = uiText.asString(app))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    // --- BİTİRİLMİŞ `deleteProduct` FONKSİYONU ---
    private fun deleteProduct(product: InventoryItem) {
        // Bu UseCase'in de Flow<Resource<Unit>> döndürdüğünü varsayıyoruz
        inventoryListUseCases.deleteProduct(product).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    // İsteğe bağlı: silme işlemi sırasında da bir yükleme durumu gösterebiliriz.
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    // Yükleme durumunu kapat. Başarı mesajı için bir SideEffect gönderilebilir.
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(
                        InventoryListContract.SideEffect.ShowSnackbar(
                            UiText.StringResource(R.string.product_deleted_successfully)
                        )
                    )
                }

                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    // 🎯 YENİ YAPI: Silme hatasını da mapper üzerinden geçiyoruz
                    val uiText = result.errorType.asInventoryErrorText()
                    _effect.send(InventoryListContract.SideEffect.ShowSnackbar(uiText))
                }
            }
        }.launchIn(viewModelScope)
    }
}