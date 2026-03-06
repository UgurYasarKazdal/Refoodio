package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

@OptIn(ExperimentalFoundationApi::class)
@Composable
 fun InventoryContent(
    state: InventoryListContract.State, onEvent: (InventoryListContract.Event) -> Unit
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.criticalItems.isEmpty() && state.sectionedItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_kitchen_message))
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 1. Üst Kısım: Tarihi Yaklaşanlar (Tam genişlik kaplayan yatay liste)
            if (state.criticalItems.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    CriticalCarousel(
                        criticalItems = state.criticalItems,
                        selectedIds = state.selectedIds, // State'den gelen seçili ID'ler
                        onToggleSelect = { id ->
                            onEvent(InventoryListContract.Event.OnToggleSelect(id))
                        },
                        onDeleteClick = { id ->
                            onEvent(InventoryListContract.Event.DeleteInventory(id))
                        })
                }
            }

            // 2. Ana Liste: Kategoriler
            state.sectionedItems.forEach { (foodGroup, items) ->
                // Header her zaman tam genişlik (2 sütun) kaplamalı
                item(span = { GridItemSpan(2) }) {
                    FoodGroupHeader(foodGroup)
                }

                // Ürünler 2'şerli yan yana dizilir
                items(items) { uiModel ->
                    InventoryItem(
                        inventoryUiModel = uiModel,
                        isSelected = state.selectedIds.contains(uiModel.id),
                        onDeleteClick = { onEvent(InventoryListContract.Event.DeleteInventory(it)) },
                        onToggleSelect = { onEvent(InventoryListContract.Event.OnToggleSelect(it)) })
                }
            }
        }
    }
}