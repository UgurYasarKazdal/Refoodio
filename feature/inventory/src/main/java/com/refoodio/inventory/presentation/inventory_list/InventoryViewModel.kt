package com.refoodio.inventory.presentation.inventory_list

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.inventory.toFoodGroup
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.domain.model.inventory.WasteLog
import com.refoodio.core.domain.use_case.inventory.InsertWasteLogUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.GetFoodByBarcodeUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InsertInventoryUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.UpdateInventoryUseCase
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
    private val updateInventory: UpdateInventoryUseCase,
    private val insertWasteLog: InsertWasteLogUseCase,
    private val app: Application
) : ViewModel() {

    private val _effect = Channel<InventoryListContract.SideEffect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(InventoryListContract.State())
    val state = _state.asStateFlow()

    // Ham liste — arama/sıralama bu üzerinden hesaplanır
    private var allItems: List<InventoryListContract.InventoryItemUiModel> = emptyList()

    // Son silinen ürünler — undo için tutulur
    private var lastDeletedItems = mutableListOf<InventoryItem>()
    // Son silme "bozuldu" kaynaklıysa true — snackbar geçince waste log yazılır
    private var lastDeleteWasWaste = false

    init {
        loadInventories()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery.trim().lowercase()
        val sort = _state.value.sortOption

        val filtered = if (query.isEmpty()) allItems
        else allItems.filter { it.name.lowercase().contains(query) }

        val sorted = when (sort) {
            InventoryListContract.SortOption.EXPIRY_DATE ->
                filtered.sortedBy { it.originalItem.expiryDate }
            InventoryListContract.SortOption.NAME ->
                filtered.sortedBy { it.name.lowercase() }
            InventoryListContract.SortOption.QUANTITY ->
                filtered.sortedByDescending { it.originalItem.quantity }
        }

        val criticalList = sorted.filter { it.isCritical }
        val groupedMap = sorted
            .groupBy { it.category.toFoodGroup() }
            .toSortedMap(compareBy { it.ordinal })

        _state.update {
            it.copy(criticalItems = criticalList, sectionedItems = groupedMap)
        }
    }

    fun handleEvent(event: InventoryListContract.Event) {
        when (event) {

            // ── Tezgah seçimi ──────────────────────────────────────────────
            is InventoryListContract.Event.OnToggleSelect -> toggleSelection(event.id)

            is InventoryListContract.Event.OnClearSelection -> {
                _state.update { it.copy(selectedIds = emptySet()) }
            }

            is InventoryListContract.Event.OnFindRecipesClick -> {
                val idsString = _state.value.selectedIds.joinToString(",")
                viewModelScope.launch {
                    _effect.send(InventoryListContract.SideEffect.NavigateToRecipesWithFilters(idsString))
                }
            }

            // ── Swipe to delete (tekil) ────────────────────────────────────
            is InventoryListContract.Event.OnSwipeDelete -> {
                val item = allItems.find { it.id == event.id }?.originalItem ?: return
                lastDeletedItems = mutableListOf(item)
                // Tezgah seçiminden de çıkar
                _state.update { it.copy(selectedIds = it.selectedIds - event.id) }
                deleteItems(listOf(event.id), undoLabel = item.name)
            }

            is InventoryListContract.Event.OnUndoDelete -> {
                // Geri al — envantere geri koy, waste log yazılmaz (flag sıfırla)
                lastDeletedItems.forEach { item ->
                    insertInventory(item).launchIn(viewModelScope)
                }
                lastDeletedItems.clear()
                lastDeleteWasWaste = false
            }

            is InventoryListContract.Event.OnDeleteConfirmed -> {
                // Snackbar geri alınmadan kapandı — bozuldu silinmesiyse waste log şimdi yaz
                if (lastDeleteWasWaste) {
                    val toLog = lastDeletedItems.toList()
                    viewModelScope.launch {
                        toLog.forEach { item ->
                            insertWasteLog(WasteLog(name = item.name, quantity = item.quantity, unit = item.unit))
                        }
                    }
                }
                lastDeletedItems.clear()
                lastDeleteWasWaste = false
            }

            is InventoryListContract.Event.OnMarkSelectedAsWasted -> {
                // BulkDeleteBar'dan "Bozuldu" — önce sil, waste log snackbar geçince yazılacak
                val idsToDelete = _state.value.deleteSelectedIds.toList()
                if (idsToDelete.isEmpty()) return
                val items = allItems.filter { idsToDelete.contains(it.id) }.map { it.originalItem }
                lastDeletedItems = items.toMutableList()
                lastDeleteWasWaste = true
                val label = buildUndoLabel(items)
                _state.update { it.copy(isInBulkDeleteMode = false, deleteSelectedIds = emptySet()) }
                deleteItems(idsToDelete, undoLabel = label)
            }

            // ── Toplu silme modu ───────────────────────────────────────────
            is InventoryListContract.Event.OnEnterBulkDeleteMode -> {
                _state.update {
                    it.copy(
                        isInBulkDeleteMode = true,
                        deleteSelectedIds = setOf(event.id)
                    )
                }
            }

            is InventoryListContract.Event.OnToggleDeleteSelect -> {
                _state.update { currentState ->
                    val newIds = if (currentState.deleteSelectedIds.contains(event.id))
                        currentState.deleteSelectedIds - event.id
                    else
                        currentState.deleteSelectedIds + event.id
                    // Hiç seçim kalmadıysa modu kapat
                    if (newIds.isEmpty()) {
                        currentState.copy(isInBulkDeleteMode = false, deleteSelectedIds = emptySet())
                    } else {
                        currentState.copy(deleteSelectedIds = newIds)
                    }
                }
            }

            is InventoryListContract.Event.OnSelectAllForDelete -> {
                val allIds = allItems.map { it.id }.toSet()
                _state.update { currentState ->
                    // Tümü zaten seçiliyse → seçimi kaldır ve modu kapat
                    if (currentState.deleteSelectedIds == allIds) {
                        currentState.copy(isInBulkDeleteMode = false, deleteSelectedIds = emptySet())
                    } else {
                        currentState.copy(deleteSelectedIds = allIds)
                    }
                }
            }

            is InventoryListContract.Event.OnExitBulkDeleteMode -> {
                _state.update { it.copy(isInBulkDeleteMode = false, deleteSelectedIds = emptySet()) }
            }

            is InventoryListContract.Event.OnConfirmBulkDelete -> {
                val idsToDelete = _state.value.deleteSelectedIds.toList()
                if (idsToDelete.isEmpty()) return
                lastDeletedItems = allItems
                    .filter { idsToDelete.contains(it.id) }
                    .map { it.originalItem }
                    .toMutableList()
                val label = buildUndoLabel(lastDeletedItems)
                _state.update { it.copy(isInBulkDeleteMode = false, deleteSelectedIds = emptySet()) }
                deleteItems(idsToDelete, undoLabel = label)
            }

            // ── Yükleme ve navigasyon ──────────────────────────────────────
            is InventoryListContract.Event.LoadInventories -> loadInventories()

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

            is InventoryListContract.Event.OnEditItem -> {
                viewModelScope.launch {
                    _effect.send(InventoryListContract.SideEffect.NavigateToEditInventory(event.itemId))
                }
            }

            // ── Kamera / barkod ────────────────────────────────────────────
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
                                    DynamicString(e.message ?: "Ürün bulunamadı")
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
                                DynamicString("${item.name} eklendi")
                            )
                        )
                        is Resource.Error -> _effect.send(
                            InventoryListContract.SideEffect.ShowSnackbar(DynamicString("Eklenemedi"))
                        )
                        else -> Unit
                    }
                }.launchIn(viewModelScope)
            }

            is InventoryListContract.Event.OnDismissBarcodeItem -> {
                _state.update { it.copy(scannedItem = null) }
            }

            // ── Grup genişletme ────────────────────────────────────────────
            is InventoryListContract.Event.ToggleGroupExpansion -> {
                val newExpanded = if (_state.value.expandedGroups.contains(event.foodGroup))
                    _state.value.expandedGroups - event.foodGroup
                else
                    _state.value.expandedGroups + event.foodGroup
                _state.update { it.copy(expandedGroups = newExpanded) }
            }

            // ── Arama / sıralama ───────────────────────────────────────────
            is InventoryListContract.Event.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }

            is InventoryListContract.Event.OnSortOptionChanged -> {
                _state.update { it.copy(sortOption = event.option) }
                applyFilters()
            }

            // ── Tüket dialogu ──────────────────────────────────────────────
            is InventoryListContract.Event.OnConsumeClick -> {
                val item = allItems.find { it.id == event.id } ?: return
                _state.update { it.copy(consumeItem = item) }
            }

            is InventoryListContract.Event.OnConsumeConfirm -> {
                val item = _state.value.consumeItem ?: return
                _state.update { it.copy(consumeItem = null) }
                val newQty = (item.originalItem.quantity - event.amount).coerceAtLeast(0.0)
                updateInventory(item.originalItem.copy(quantity = newQty)).onEach { result ->
                    if (result is Resource.Error) {
                        _effect.send(
                            InventoryListContract.SideEffect.ShowSnackbar(DynamicString("Güncellenemedi"))
                        )
                    }
                }.launchIn(viewModelScope)
            }

            is InventoryListContract.Event.OnConsumeDismiss -> {
                _state.update { it.copy(consumeItem = null) }
            }

            is InventoryListContract.Event.OnBulkConsume -> {
                event.amounts.filter { it.value > 0.0 }.forEach { (id, amount) ->
                    val item = allItems.find { it.id == id } ?: return@forEach
                    val newQty = (item.originalItem.quantity - amount).coerceAtLeast(0.0)
                    updateInventory(item.originalItem.copy(quantity = newQty)).launchIn(viewModelScope)
                }
                _state.update { it.copy(selectedIds = emptySet()) }
            }
        }
    }

    private fun buildUndoLabel(items: List<InventoryItem>): String {
        val names = items.take(2).joinToString(", ") { it.name }
        return if (items.size > 2) "$names ve ${items.size - 2} diğeri" else names
    }

    private fun loadInventories() {
        inventoryListUseCases.getInventories().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    viewModelScope.launch(Dispatchers.Default) {
                        val now = System.currentTimeMillis()
                        val mapped = result.data.map { item ->
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

                        allItems = mapped
                        _state.update { it.copy(isLoading = false) }
                        applyFilters()
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
            val newSelectedIds = if (currentState.selectedIds.contains(foodId))
                currentState.selectedIds - foodId
            else
                currentState.selectedIds + foodId
            currentState.copy(selectedIds = newSelectedIds)
        }
    }

    private fun deleteItems(ids: List<Int>, undoLabel: String = "") {
        inventoryListUseCases.deleteSelectedInventories(ids).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, selectedIds = it.selectedIds - ids.toSet()) }
                    if (undoLabel.isNotEmpty()) {
                        _effect.send(InventoryListContract.SideEffect.ShowUndoDeleteSnackbar(undoLabel))
                    } else {
                        _effect.send(
                            InventoryListContract.SideEffect.ShowSnackbar(
                                UiText.StringResource(R.string.inventory_deleted_successfully)
                            )
                        )
                    }
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
