package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
    state: InventoryListContract.State,
    onEvent: (InventoryListContract.Event) -> Unit
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.criticalItems.isEmpty() && state.sectionedItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.empty_kitchen_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onEvent(InventoryListContract.Event.NavigateAddInventory) }) {
                    Text("İlk Ürünü Ekle")
                }
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.criticalItems.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    CriticalCarousel(
                        criticalItems = state.criticalItems,
                        selectedIds = state.selectedIds,
                        onToggleSelect = { id ->
                            onEvent(InventoryListContract.Event.OnToggleSelect(id))
                        })
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            state.sectionedItems.forEach { (foodGroup, items) ->
                val isExpanded = state.expandedGroups.contains(foodGroup)
                item(span = { GridItemSpan(2) }) {
                    FoodGroupHeader(
                        foodGroup = foodGroup,
                        itemCount = items.size,
                        isExpanded = isExpanded,
                        onHeaderClick = {
                            onEvent(InventoryListContract.Event.ToggleGroupExpansion(foodGroup))
                        }
                    )
                }

                if (isExpanded) {
                    items(items, key = { it.id }) { uiModel ->
                        Box(modifier = Modifier.animateItem()) {
                            InventoryItem(
                                item = uiModel,
                                isSelected = state.selectedIds.contains(uiModel.id),
                                onToggleSelect = { onEvent(InventoryListContract.Event.OnToggleSelect(it)) },
                                onEditItem = { onEvent(InventoryListContract.Event.OnEditItem(it)) },
                                onDeleteItem = { onEvent(InventoryListContract.Event.OnDeleteSingleItem(it)) },
                                onConsumeItem = { onEvent(InventoryListContract.Event.OnConsumeClick(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}