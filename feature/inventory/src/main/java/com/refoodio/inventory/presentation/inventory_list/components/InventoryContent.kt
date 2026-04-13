package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refoodio.core.ui.components.EmptyStateView
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
                    tezgahItems = state.tezgahItems,
                    onItemTapped = { id ->
                        onEvent(InventoryListContract.Event.OnItemTapped(id))
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
                items(rows, key = { "${foodGroup.name}_row_${rows.indexOf(it)}" }) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { uiModel ->
                            key(uiModel.id) {
                                // Swipe to delete — bulk delete modunda devre dışı
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (value == SwipeToDismissBoxValue.EndToStart &&
                                            !state.isInBulkDeleteMode
                                        ) {
                                            onEvent(InventoryListContract.Event.OnSwipeDelete(uiModel.id))
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )

                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    SwipeToDismissBox(
                                        state = dismissState,
                                        enableDismissFromStartToEnd = false,
                                        enableDismissFromEndToStart = !state.isInBulkDeleteMode,
                                        backgroundContent = {
                                            // Kırmızı arka plan — sola kaydırınca görünür
                                            val bgColor by animateColorAsState(
                                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                                    MaterialTheme.colorScheme.error
                                                else
                                                    MaterialTheme.colorScheme.errorContainer,
                                                label = "swipe_bg"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp)
                                                    .background(
                                                        bgColor,
                                                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.CenterEnd
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Sil",
                                                    tint = MaterialTheme.colorScheme.onError,
                                                    modifier = Modifier
                                                        .padding(end = 16.dp)
                                                        .size(24.dp)
                                                )
                                            }
                                        }
                                    ) {
                                        InventoryItem(
                                            item = uiModel,
                                            tezgahQuantity = state.tezgahItems[uiModel.id],
                                            onItemTapped = {
                                                onEvent(InventoryListContract.Event.OnItemTapped(it))
                                            },
                                            isInBulkDeleteMode = state.isInBulkDeleteMode,
                                            isDeleteSelected = state.deleteSelectedIds.contains(uiModel.id),
                                            onToggleDeleteSelect = {
                                                onEvent(InventoryListContract.Event.OnToggleDeleteSelect(it))
                                            },
                                            onEnterBulkDeleteMode = {
                                                onEvent(InventoryListContract.Event.OnEnterBulkDeleteMode(it))
                                            }
                                        )
                                    }
                                }
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
