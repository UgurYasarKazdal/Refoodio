package com.refoodio.inventory.presentation.inventory_list

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.inventory.toFoodGroup
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.domain.use_case.inventory.addInventory.GetFoodByBarcodeUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InsertInventoryUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.InventoryListUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.formatExpiryDate
import com.refoodio.core.ui.util.UiText
import com.refoodio.core.ui.util.UiText.DynamicString
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
    private val inventoryListUseCases: InventoryListUseCases,
    private val getFoodByBarcode: GetFoodByBarcodeUseCase,
    private val insertInventory: InsertInventoryUseCase,
    private val app: Application
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
            is InventoryListContract.Event.OnRequestDelete -> {
                _state.update { it.copy(showDeleteConfirmation = true) }
            }

            is InventoryListContract.Event.OnDeleteDismissed -> {
                _state.update { it.copy(showDeleteConfirmation = false) }
            }

            is InventoryListContract.Event.DeleteInventory -> {
                _state.update { it.copy(showDeleteConfirmation = false) }
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

            is InventoryListContract.Event.NavigateToReceiptScan -> {
                viewModelScope.launch {
                    _effect.send(InventoryListContract.SideEffect.NavigateToReceiptScan)
                }
            }

            is InventoryListContract.Event.OnToggleCamera -> {
                _state.update { it.copy(isCameraVisible = !it.isCameraVisible) }
            }

            is InventoryListContract.Event.OnBarcodeDetected -> {
                _state.update { it.copy(isCameraVisible = false, isBarcodeLoading = true) }
                viewModelScope.launch {
                    getFoodByBarcode(event.barcode)
                        .onSuccess { foodItem ->
                            val unit = FoodUnit.entries.find {
                                it.name.equals(foodItem.unit, ignoreCase = true)
                            } ?: FoodUnit.PIECE
                            val category = FoodCategory.fromId(foodItem.categoryId)
                            val expiryDate = System.currentTimeMillis() +
                                    foodItem.defaultShelfLife * 24 * 60 * 60 * 1000L
                            _state.update {
                                it.copy(
                                    isBarcodeLoading = false,
                                    scannedItem = InventoryItem(
                                        name = foodItem.name,
                                        quantity = 1.0,
                                        unit = unit,
                                        category = category,
                                        expiryDate = expiryDate
                                    )
                                )
                            }
                        }
                        .onFailure { e ->
                            _state.update { it.copy(isBarcodeLoading = false) }
                            _effect.send(
                                InventoryListContract.SideEffect.ShowSnackbar(
                                    UiText.DynamicString(e.message ?: "Ürün bulunamadı")
                                )
                            )
                        }
                }
            }

            is InventoryListContract.Event.OnScannedItemNameChanged ->
                _state.update { it.copy(scannedItem = it.scannedItem?.copy(name = event.name)) }

            is InventoryListContract.Event.OnScannedItemQuantityChanged ->
                _state.update { it.copy(scannedItem = it.scannedItem?.copy(quantity = event.quantity)) }

            is InventoryListContract.Event.OnScannedItemUnitChanged ->
                _state.update { it.copy(scannedItem = it.scannedItem?.copy(unit = event.unit)) }

            is InventoryListContract.Event.OnScannedItemCategoryChanged ->
                _state.update { it.copy(scannedItem = it.scannedItem?.copy(category = event.category)) }

            is InventoryListContract.Event.OnConfirmBarcodeItem -> {
                val item = _state.value.scannedItem ?: return
                _state.update { it.copy(scannedItem = null) }
                insertInventory(item).onEach { result ->
                    when (result) {
                        is Resource.Success -> _effect.send(
                            InventoryListContract.SideEffect.ShowSnackbar(
                                UiText.DynamicString("${item.name} eklendi")
                            )
                        )
                        is Resource.Error -> _effect.send(
                            InventoryListContract.SideEffect.ShowSnackbar(
                                UiText.DynamicString("Eklenemedi")
                            )
                        )
                        else -> Unit
                    }
                }.launchIn(viewModelScope)
            }

            is InventoryListContract.Event.OnDismissBarcodeItem -> {
                _state.update { it.copy(scannedItem = null) }
            }

            is InventoryListContract.Event.ToggleGroupExpansion -> {
                val newExpandedGroups = if (_state.value.expandedGroups.contains(event.foodGroup)) {
                    _state.value.expandedGroups - event.foodGroup
                } else {
                    _state.value.expandedGroups + event.foodGroup
                }
                _state.update { it.copy(expandedGroups = newExpandedGroups) }
            }

            is InventoryListContract.Event.OnEditItem -> {
                viewModelScope.launch {
                    _effect.send(InventoryListContract.SideEffect.NavigateToEditInventory(event.itemId))
                }
            }

            is InventoryListContract.Event.OnDeleteSingleItem -> {
                deleteInventory(listOf(event.id))
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
                        val now = System.currentTimeMillis()
                        val allItems = result.data.map { item ->
                            val group = item.category.toFoodGroup()
                            val baseColor = Color(app.resources.getColor(group.colorResId, null))
                            val daysLeft = (item.expiryDate - now) / (24 * 60 * 60 * 1000L)
                            val urgency = when {
                                daysLeft <= 3 -> InventoryListContract.UrgencyLevel.CRITICAL
                                daysLeft <= 7 -> InventoryListContract.UrgencyLevel.WARNING
                                else -> InventoryListContract.UrgencyLevel.NORMAL
                            }

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
                                urgencyLevel = urgency,
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