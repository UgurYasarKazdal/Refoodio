package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.components.InventoryContent
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