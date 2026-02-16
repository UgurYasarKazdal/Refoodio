package com.refoodio.inventory.presentation.add_inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.inventory.presentation.util.asInventoryErrorText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class InventoryAddViewModel @Inject constructor(private val inventoryAddUseCases: InventoryAddUseCases) :
    ViewModel() {
    private val _query = MutableStateFlow("")

    private val _state = MutableStateFlow(InventoryAddContract.State())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state: StateFlow<InventoryAddContract.State> =
        _query.debounce(300)
            .filter { it.length >= 2 }
            .flatMapLatest { query ->
                // RACE CONDITION YAKALAMA:
                // Kullanıcı "Elm" yazıp hemen "Elma" yazarsa,
                // "Elm" için başlayan eski akış burada anında iptal edilir.
                inventoryAddUseCases.suggestionsUseCase(query)
                    .map { InventoryAddContract.State(suggestions = it) }
            }.stateIn(
                scope = this.viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = InventoryAddContract.State()
            )

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    private fun addProduct(product: InventoryItem) {
        inventoryAddUseCases.insertProduct(product).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, errorMessage = null) }
                    // Burada başarılı sinyali (Event) gönderebilirsin
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