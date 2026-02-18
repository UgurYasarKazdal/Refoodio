package com.refoodio.inventory.presentation.inventory_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.refoodio.inventory.presentation.inventory_list.components.ProductItem
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateToAddInventory: () -> Unit // <-- Burası yeni!
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- DEĞİŞİKLİK 1: SnackbarHostState'i oluşturun ---
    val snackbarHostState = remember { SnackbarHostState() }
    // Snackbar'ı göstermek için bir coroutine scope'a ihtiyacımız olacak
    val scope = rememberCoroutineScope()

    // LaunchedEffect: Sadece bir kereliğine çalışır ve coroutine'i başlatır.
    // viewModel.effect akışını dinler. Ekran recompose olsa bile tekrar çalışmaz.
    // 'true' veya 'Unit' gibi anahtarlar, coroutine'in ekranın yaşam döngüsü boyunca aktif kalmasını sağlar.
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            // ViewModel'den gelen her SideEffect için bu blok çalışır.
            when (effect) {
                is InventoryListContract.SideEffect.ShowSnackbar -> {
                    val message = effect.message.asString(context)
                    // --- DEĞİŞİKLİK 2: Snackbar'ı scope ile gösterin ---
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = message, duration = SnackbarDuration.Short
                        )
                    }
                }

                is InventoryListContract.SideEffect.ProductDeleted -> {
                    val message = context.getString(R.string.product_deleted_successfully)
                    // --- DEĞİŞİKLİK 2: Snackbar'ı scope ile gösterin ---
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.handleEvent(InventoryListContract.Event.NavigateAddInventory) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }) { padding ->
        // Ana iskelet, içeriği çizme sorumluluğunu stateless komponente devrediyor.
        InventoryContent(
            state = state,
            padding = padding,
            onEvent = viewModel::handleEvent // viewModel.handleEvent fonksiyonunu doğrudan referans olarak veriyoruz.
        )

    }
}


// --- YENİ STATELESS COMPOSABLE ---
// Bu fonksiyon private olabilir, çünkü sadece InventoryScreen içinde kullanılıyor.
@Composable
private fun InventoryContent(
    state: InventoryListContract.State,
    padding: PaddingValues,
    onEvent: (InventoryListContract.Event) -> Unit // Olayları dışarıya bildirir.
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        if (state.products.isEmpty()) {
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
                contentPadding = PaddingValues(RefoodioTheme.spacing.large), // Bunu Dimens ile değiştirebiliriz.
                verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.medium)
            ) {
                items(
                    items = state.products,
                    key = { it.id ?: it.hashCode() }
                ) { product ->
                    ProductItem(
                        product = product,
                        onDeleteClick = { onEvent(InventoryListContract.Event.DeleteProduct(product)) } // Olayı yukarı bildirir.
                    )
                }
            }
        }
    }
}