package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.domain.model.inventory.FoodGroup
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.components.CriticalCarousel
import com.refoodio.inventory.presentation.inventory_list.components.InventoryItem
import com.refoodio.inventory.presentation.inventory_list.components.RecipeWizardBar
import kotlinx.coroutines.launch


@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateToAddInventory: () -> Unit,
    onNavigateToRecipes: (String) -> Unit // Navigasyon için yeni parametre
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InventoryListContract.SideEffect.ShowSnackbar -> {
                    val message = effect.message.asString(context)
                    scope.launch {
                        snackbarHostState.showSnackbar(message = message)

                    }
                }

                is InventoryListContract.SideEffect.NavigateToAddInventory -> {
                    onNavigateToAddInventory()
                }

                is InventoryListContract.SideEffect.NavigateToRecipesWithFilters -> {
                    onNavigateToRecipes(effect.selectedIds)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            // Eğer seçim varsa FAB'ı gizleyebilir veya yukarı kaydırabilirsin.
            // Şimdilik standart bırakıyoruz.
            FloatingActionButton(onClick = { viewModel.handleEvent(InventoryListContract.Event.NavigateAddInventory) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            InventoryContent(
                state = state, onEvent = viewModel::handleEvent
            )

            // Sihirbaz Barı en üst katmanda ve en altta
            RecipeWizardBar(
                selectedCount = state.selectedIds.size,
                onFindRecipesClick = { viewModel.handleEvent(InventoryListContract.Event.OnFindRecipesClick) },
                onClearSelection = { viewModel.handleEvent(InventoryListContract.Event.OnClearSelection) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InventoryContent(
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

@Composable
fun FoodGroupHeader(foodGroup: FoodGroup) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Text(
            text = stringResource(foodGroup.titleResId), // Daha önce eklediğimiz string ID'si
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.error // Kritik bölüm için vurgu rengi
    )
}