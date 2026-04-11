package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import com.refoodio.core.ui.components.EmptyStateView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            EmptyStateView(
                emoji = "🧺",
                title = "Mutfağın boş görünüyor",
                description = "Henüz envantere ürün eklemedin.\nAlışverişten döner dönmez ürünlerini ekle,\nson kullanma tarihlerini takip et.",
                actionLabel = "İlk Ürünü Ekle",
                onAction = { onEvent(InventoryListContract.Event.NavigateAddInventory) }
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Kritik ürünler carousel ───────────────────────────────────────
        if (state.criticalItems.isNotEmpty()) {
            item {
                CriticalCarousel(
                    criticalItems = state.criticalItems,
                    selectedIds = state.selectedIds,
                    onToggleSelect = { id ->
                        onEvent(InventoryListContract.Event.OnToggleSelect(id))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ── Kategoriler: sticky header + expandable ürünler ───────────────
        state.sectionedItems.forEach { (foodGroup, items) ->
            val isExpanded = state.expandedGroups.contains(foodGroup)

            stickyHeader(key = "header_${foodGroup.name}") {
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
                val rows = items.chunked(2)
                items(rows, key = { "${foodGroup.name}_${rows.indexOf(it)}" }) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { uiModel ->
                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                InventoryItem(
                                    item = uiModel,
                                    isSelected = state.selectedIds.contains(uiModel.id),
                                    onToggleSelect = {
                                        onEvent(InventoryListContract.Event.OnToggleSelect(it))
                                    },
                                    onEditItem = {
                                        onEvent(InventoryListContract.Event.OnEditItem(it))
                                    },
                                    onDeleteItem = {
                                        onEvent(InventoryListContract.Event.OnDeleteSingleItem(it))
                                    },
                                    onConsumeItem = {
                                        onEvent(InventoryListContract.Event.OnConsumeClick(it))
                                    }
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item(key = "spacer_${foodGroup.name}") {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
