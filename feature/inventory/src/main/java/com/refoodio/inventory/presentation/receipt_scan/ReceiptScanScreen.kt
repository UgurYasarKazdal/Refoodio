package com.refoodio.inventory.presentation.receipt_scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.ui.components.RefoodioPrimaryButton
import com.refoodio.core.ui.components.camera.ReceiptCameraPreview
import com.refoodio.core.ui.theme.RefoodioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScanScreen(
    viewModel: ReceiptScanViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            viewModel.handleEvent(ReceiptScanContract.Event.OnNavigateBack)
        }
    }

    LaunchedEffect(Unit) {
        val permissionResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ReceiptScanContract.SideEffect.NavigateBack -> onNavigateBack()
                is ReceiptScanContract.SideEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (state.phase == ReceiptScanContract.Phase.REVIEWING) {
                TopAppBar(
                    title = { Text("Fiş Sonuçları") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.handleEvent(ReceiptScanContract.Event.OnNavigateBack) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        when (state.phase) {
            ReceiptScanContract.Phase.SCANNING -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ReceiptCameraPreview(
                        isLoading = false,
                        onPhotoCaptured = { bytes ->
                            viewModel.handleEvent(ReceiptScanContract.Event.OnPhotoCaptured(bytes))
                        }
                    )
                    IconButton(
                        onClick = { viewModel.handleEvent(ReceiptScanContract.Event.OnNavigateBack) },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(RefoodioTheme.spacing.medium)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }

            ReceiptScanContract.Phase.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(RefoodioTheme.spacing.medium))
                        Text("Fiş analiz ediliyor...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            ReceiptScanContract.Phase.REVIEWING -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = RefoodioTheme.spacing.large),
                    verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)
                ) {
                    OutlinedTextField(
                        value = state.storeName,
                        onValueChange = { viewModel.handleEvent(ReceiptScanContract.Event.OnStoreNameChanged(it)) },
                        label = { Text("Market Adı") },
                        placeholder = { Text("Migros, A101, BİM...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = RefoodioTheme.spacing.small)
                    )

                    Text(
                        text = "${state.scannedItems.size} ürün bulundu",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = RefoodioTheme.spacing.small)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.medium)
                    ) {
                        itemsIndexed(state.scannedItems) { index, item ->
                            ReceiptItemCard(
                                item = item,
                                onNameChanged = { viewModel.handleEvent(ReceiptScanContract.Event.OnItemNameChanged(index, it)) },
                                onQuantityChanged = { viewModel.handleEvent(ReceiptScanContract.Event.OnItemQuantityChanged(index, it)) },
                                onUnitChanged = { viewModel.handleEvent(ReceiptScanContract.Event.OnItemUnitChanged(index, it)) },
                                onCategoryChanged = { viewModel.handleEvent(ReceiptScanContract.Event.OnItemCategoryChanged(index, it)) },
                                onPriceChanged = { viewModel.handleEvent(ReceiptScanContract.Event.OnItemPriceChanged(index, it)) },
                                onRemove = { viewModel.handleEvent(ReceiptScanContract.Event.OnItemRemoved(index)) }
                            )
                        }
                    }

                    RefoodioPrimaryButton(
                        text = "Envantere Ekle (${state.scannedItems.size})",
                        onClick = { viewModel.handleEvent(ReceiptScanContract.Event.OnConfirmItems) },
                        isEnabled = state.scannedItems.isNotEmpty(),
                        modifier = Modifier.padding(vertical = RefoodioTheme.spacing.medium)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceiptItemCard(
    item: InventoryItem,
    onNameChanged: (String) -> Unit,
    onQuantityChanged: (Double) -> Unit,
    onUnitChanged: (FoodUnit) -> Unit,
    onCategoryChanged: (FoodCategory) -> Unit,
    onPriceChanged: (Double?) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = RefoodioTheme.stroke.standard)
    ) {
        Column(
            modifier = Modifier.padding(RefoodioTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = onNameChanged,
                    label = { Text("Ürün Adı") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Kaldır")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)) {
                OutlinedTextField(
                    value = item.quantity.toString(),
                    onValueChange = { onQuantityChanged(it.toDoubleOrNull() ?: item.quantity) },
                    label = { Text("Miktar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                FoodUnitDropdown(
                    selected = item.unit,
                    onSelected = onUnitChanged,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = item.price?.toString() ?: "",
                onValueChange = { onPriceChanged(it.toDoubleOrNull()) },
                label = { Text("Fiyat (₺)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            FoodCategoryDropdown(
                selected = item.category,
                onSelected = onCategoryChanged,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodUnitDropdown(
    selected: FoodUnit,
    onSelected: (FoodUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Birim") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            FoodUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.name) },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodCategoryDropdown(
    selected: FoodCategory,
    onSelected: (FoodCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kategori") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            FoodCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
