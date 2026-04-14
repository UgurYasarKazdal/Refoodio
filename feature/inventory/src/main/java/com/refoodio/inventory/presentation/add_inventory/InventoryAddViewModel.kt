package com.refoodio.inventory.presentation.add_inventory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.domain.util.daysToMillis
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.core.ui.util.UiText
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.util.asInventoryErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
import kotlin.math.round

@HiltViewModel
class InventoryAddViewModel @Inject constructor(
    private val inventoryAddUseCases: InventoryAddUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _effect = Channel<InventoryAddContract.SideEffect>()
    val effect = _effect.receiveAsFlow()

    private val searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(InventoryAddContract.State())

    init {
        observeQuery()
        tryLoadEditItem()
    }

    private fun tryLoadEditItem() {
        val route = runCatching {
            savedStateHandle.toRoute<NavigationRoutes.InventoryEditRoute>()
        }.getOrNull() ?: return

        val itemId = route.itemId
        if (itemId == -1) return

        viewModelScope.launch {
            val item = inventoryAddUseCases.getInventoryById(itemId) ?: return@launch
            _state.update {
                it.copy(
                    isEditMode = true,
                    editItemId = itemId,
                    searchQuery = item.name,
                    form = it.form.copy(
                        selectedFoodName = item.name,
                        quantity = item.quantity,
                        unit = item.unit,
                        category = item.category,
                        expiryDate = item.expiryDate
                    )
                )
            }
        }
    }

    private fun observeQuery() {
        searchQuery.debounce(300).filter { it.length >= 2 }.flatMapLatest { query ->
            inventoryAddUseCases.suggestionsUseCase("$query*")
        }.onEach { results ->
            _state.update { it.copy(suggestions = results) }
        }.launchIn(viewModelScope)
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
                val calculatedExpiry =
                    System.currentTimeMillis() + event.food.defaultShelfLife.daysToMillis

                _state.update {
                    it.copy(
                        form = it.form.copy(
                            selectedFoodName = event.food.name,
                            shelfLifeDays = event.food.defaultShelfLife,
                            expiryDate = calculatedExpiry,
                            selectedCategory = event.food.category,
                            quantity = 1.0,
                            category = FoodCategory.fromId(event.food.categoryId)

                        ), suggestions = emptyList()
                    )
                }
            }

            InventoryAddContract.Event.OnIncrementQuantity -> {
                val nextValue = _state.value.form.quantity + _state.value.form.unit.step
                // Round to one decimal place
                updateForm { form -> form.copy(quantity = round(nextValue * 10) / 10.0) }
            }

            InventoryAddContract.Event.OnDecrementQuantity -> {
                val nextValue = _state.value.form.quantity - _state.value.form.unit.step
                // Round to one decimal place
                if (_state.value.form.quantity > 1) {
                    updateForm { form -> form.copy(quantity = round(nextValue * 10) / 10.0) }
                }
            }

            is InventoryAddContract.Event.OnDateChanged -> {
                updateForm { form -> form.copy(expiryDate = event.date) }
            }

            InventoryAddContract.Event.OnSaveInventory -> {
                saveInventory()
            }

            is InventoryAddContract.Event.OnUnitSelected -> {
                updateForm { form -> form.copy(unit = event.unit) }
            }

            is InventoryAddContract.Event.OnQuantitySelected -> {
                updateForm { form -> form.copy(quantity = event.unit) }
            }

            is InventoryAddContract.Event.OnPackageContentChanged -> {
                updateForm { form -> form.copy(packageContent = event.content) }
            }

            is InventoryAddContract.Event.OnPackageContentUnitSelected -> {
                updateForm { form -> form.copy(packageContentUnit = event.unit) }
            }

            is InventoryAddContract.Event.OnBarcodeScanned -> {
                searchInventoryByBarcode(event.barcode)
            }

            is InventoryAddContract.Event.OnToggleCamera -> {
                _state.update { it.copy(isCameraVisible = !it.isCameraVisible) }
            }

            is InventoryAddContract.Event.OnPermissionDenied -> {
                viewModelScope.launch {
                    _effect.send(
                        InventoryAddContract.SideEffect.ShowSnackBar(
                            UiText.StringResource(R.string.need_permission_to_camera)
                        )
                    )
                }
            }
        }
    }

    private fun updateForm(update: (InventoryAddContract.InventoryForm) -> InventoryAddContract.InventoryForm) {
        _state.update { it.copy(form = update(it.form)) }
    }

    private fun searchInventoryByBarcode(barcode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            inventoryAddUseCases.getFoodByBarcodeUseCase(barcode).onSuccess { foodItem ->
                _state.update {
                    it.copy(
                        form = it.form.copy(
                            selectedFoodName = foodItem.name,
                            selectedCategory = foodItem.category,
                            category = FoodCategory.fromId(foodItem.categoryId)
                        ), isCameraVisible = false, isLoading = false
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveInventory() {
        val currentState = _state.value
        if (currentState.isLoading) return
        if (currentState.form.selectedFoodName.isBlank()) return

        val (finalQuantity, finalUnit) = if (
            currentState.form.unit == com.refoodio.core.domain.model.inventory.FoodUnit.PACK &&
            currentState.form.packageContent != null &&
            currentState.form.packageContent > 0.0
        ) {
            currentState.form.packageContent to currentState.form.packageContentUnit
        } else {
            currentState.form.quantity to currentState.form.unit
        }

        val item = InventoryItem(
            id = if (currentState.isEditMode) currentState.editItemId else 0,
            name = currentState.form.selectedFoodName,
            expiryDate = currentState.form.expiryDate ?: System.currentTimeMillis(),
            quantity = finalQuantity,
            unit = finalUnit,
            category = currentState.form.category,
        )

        if (currentState.isEditMode) {
            // Güncelleme — side_units zaten mevcut, yeniden çekmiyoruz
            inventoryAddUseCases.updateInventory(item).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> onSaveSuccess()
                    is Resource.Error -> _state.update { it.copy(isLoading = false) }
                }
            }.launchIn(viewModelScope)
        } else {
            // Yeni kayıt — başarı sonrası arka planda Gemini'den yan birim önerileri alınır
            inventoryAddUseCases.insertInventory(item).onEach { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        onSaveSuccess()
                    }
                    is Resource.Error -> _state.update { it.copy(isLoading = false) }
                }
            }.launchIn(viewModelScope)
        }
    }

    private suspend fun onSaveSuccess() {
        _state.update {
            it.copy(
                isLoading = false,
                form = InventoryAddContract.InventoryForm(),
                suggestions = emptyList(),
                errorMessage = null
            )
        }
        _effect.send(InventoryAddContract.SideEffect.NavigateBack)
    }
}