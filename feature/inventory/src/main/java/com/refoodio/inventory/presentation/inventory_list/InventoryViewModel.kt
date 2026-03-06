package com.refoodio.inventory.presentation.inventory_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.inventory.toFoodGroup
import com.refoodio.core.domain.use_case.inventory.inventoryList.InventoryListUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.toReadableDate
import com.refoodio.core.ui.util.UiText
import com.refoodio.core.ui.util.formatQuantity
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
    private val inventoryListUseCases: InventoryListUseCases, private val app: Application
) : ViewModel() {

    private val _effect = Channel<InventoryListContract.SideEffect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(InventoryListContract.State())
    val state = _state.asStateFlow()

    init {
        loadInventories()
    }

    fun handleEvent(event: InventoryListContract.Event) {
        when (event) {
            is InventoryListContract.Event.DeleteInventory -> {
                // ID üzerinden orijinal item'ı bulup siliyoruz
                val currentItems = _state.value.sectionedItems.values.flatten()
                val itemToDelete = currentItems.find { it.id == event.id }?.originalItem

                itemToDelete?.let { deleteInventory(it) }

                viewModelScope.launch {
                    _effect.send(
                        InventoryListContract.SideEffect.ShowSnackbar(
                            UiText.StringResource(
                                R.string.inventory_deleted_successfully
                            )
                        )
                    )
                }
            }

            is InventoryListContract.Event.LoadInventories -> loadInventories()

            is InventoryListContract.Event.OnToggleSelect -> toggleSelection(event.id)

            is InventoryListContract.Event.OnClearSelection -> {
                _state.update { it.copy(selectedIds = emptySet()) }
            }

            is InventoryListContract.Event.OnFindRecipesClick -> {
                val idsString = _state.value.selectedIds.joinToString(",")
                viewModelScope.launch {
                    _effect.send(
                        InventoryListContract.SideEffect.NavigateToRecipesWithFilters(
                            idsString
                        )
                    )
                }
            }

            is InventoryListContract.Event.NavigateAddInventory -> {
                viewModelScope.launch {
                    _effect.send(InventoryListContract.SideEffect.NavigateToAddInventory)
                }
            }
            // Eski event yapısını contract'a göre güncelledik
            else -> Unit
        }
    }

    private fun loadInventories() {
        inventoryListUseCases.getInventories().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    val allItems = result.data.map { item ->
                        InventoryListContract.InventoryItemUiModel(
                            id = item.id,
                            name = item.name,
                            quantityText = UiText.StringResource(
                                R.string.quantity, item.quantity.formatQuantity()
                            ),
                            formattedDate = UiText.StringResource(
                                R.string.add_inventory_expiry_date, item.expiryDate.toReadableDate()
                            ),
                            isCritical = item.isNearExpiry(),
                            originalItem = item,
                            category = item.category,
                            unit = item.unit
                        )
                    }

                    // Hiyerarşik Dağıtım (Race Condition önlemi: immutable kopyalar üzerinden işlem)
                    val criticalList = allItems.filter { it.isCritical }
                    val groupedMap = allItems.groupBy { it.category.toFoodGroup() }
                        .toSortedMap(compareBy { it.ordinal }) // Enum sırasına göre düzenli gösterim

                    _state.update {
                        it.copy(
                            isLoading = false,
                            criticalItems = criticalList,
                            sectionedItems = groupedMap
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

    private fun toggleSelection(foodId: Int) {
        _state.update { currentState ->
            val newSelectedIds = if (currentState.selectedIds.contains(foodId)) {
                currentState.selectedIds - foodId
            } else {
                currentState.selectedIds + foodId
            }
            currentState.copy(selectedIds = newSelectedIds)
        }
    }

    private fun deleteInventory(inventoryItem: InventoryItem) {
        inventoryListUseCases.deleteInventory(inventoryItem).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    // Ürün silindiğinde seçili listesinden de çıkarılmalı
                    _state.update {
                        it.copy(
                            isLoading = false, selectedIds = it.selectedIds - inventoryItem.id
                        )
                    }
                    _effect.send(
                        InventoryListContract.SideEffect.ShowSnackbar(
                            UiText.StringResource(R.string.inventory_deleted_successfully)
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