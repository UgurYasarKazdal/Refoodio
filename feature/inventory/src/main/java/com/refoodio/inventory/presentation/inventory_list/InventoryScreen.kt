package com.refoodio.inventory.presentation.inventory_list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.components.InventoryItem
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(), onNavigateToAddInventory: () -> Unit
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
                        snackbarHostState.showSnackbar(
                            message = message, duration = SnackbarDuration.Short
                        )
                    }

                }

                is InventoryListContract.SideEffect.InventoryDeleted -> {
                    val message = context.getString(R.string.inventory_deleted_successfully)
                    scope.launch {
                        snackbarHostState.showSnackbar(message = message)

                    }
                }

                is InventoryListContract.SideEffect.NavigateToAddInventory -> {
                    onNavigateToAddInventory()
                }
            }
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(modifier = Modifier.wrapContentSize(), hostState = snackbarHostState)
    }, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.handleEvent(InventoryListContract.Event.NavigateAddInventory) }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
        }
    }) { padding ->
        InventoryContent(
            state = state, padding = padding, onEvent = viewModel::handleEvent
        )

    }
}

@Composable
private fun InventoryContent(
    state: InventoryListContract.State,
    padding: PaddingValues,
    onEvent: (InventoryListContract.Event) -> Unit
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        if (state.inventories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_kitchen_message))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(RefoodioTheme.spacing.large),
                verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.medium)
            ) {
                items(
                    items = state.inventories, key = { it.id ?: it.hashCode() }) { inventoryItem ->
                    InventoryItem(
                        inventoryUiModel = inventoryItem,
                        onDeleteClick = { onEvent(InventoryListContract.Event.DeleteInventory(inventoryItem.originalItem)) })
                }
            }
        }
    }
}