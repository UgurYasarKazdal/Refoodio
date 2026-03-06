package com.refoodio.inventory.presentation.inventory_list

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.toFoodGroup
import com.refoodio.core.domain.use_case.inventory.inventoryList.InventoryListUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.formatExpiryDate
import com.refoodio.core.ui.util.UiText
import com.refoodio.core.ui.util.formatQuantity
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.util.asInventoryErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
                val idsToDelete = state.value.selectedIds.toList()
                if (idsToDelete.isNotEmpty()) {
                    deleteInventory(idsToDelete)
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

            is InventoryListContract.Event.ToggleGroupExpansion -> {
                val newExpandedGroups = if (_state.value.expandedGroups.contains(event.foodGroup)) {
                    _state.value.expandedGroups - event.foodGroup
                } else {
                    _state.value.expandedGroups + event.foodGroup
                }
                _state.update { it.copy(expandedGroups = newExpandedGroups) }
            }

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
                    viewModelScope.launch(Dispatchers.Default) {
                        // Hesaplamayı arka plana al
                        val allItems = result.data.map { item ->
                            val group = item.category.toFoodGroup()
                            val baseColor = Color(
                                app.resources.getColor(
                                    group.colorResId, null
                                )
                            ) // Bir kez hesapla

                            InventoryListContract.InventoryItemUiModel(
                                id = item.id,
                                name = item.name,
                                quantityText = UiText.StringResource(
                                    R.string.quantity_short, item.quantity.formatQuantity()
                                ),
                                formattedDate = UiText.StringResource(
                                    R.string.add_inventory_expiry_date_short,
                                    item.expiryDate.formatExpiryDate()
                                ),
                                isCritical = item.isNearExpiry(),
                                originalItem = item,
                                category = item.category,
                                unit = item.unit,
                                color = baseColor,
                                backgroundColor = baseColor.copy(alpha = 0.25f),
                                groupIcon = group.iconResId
                            )
                        }

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

    private fun deleteInventory(inventories: List<Int>) {
        inventoryListUseCases.deleteSelectedInventories(inventories).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, selectedIds = emptySet()) }
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