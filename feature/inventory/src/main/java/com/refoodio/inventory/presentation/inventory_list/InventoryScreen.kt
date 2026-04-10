package com.refoodio.inventory.presentation.inventory_list

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.recipe.FoodCategory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.ui.components.camera.CameraPreview
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.inventory_list.components.InventoryContent
import com.refoodio.inventory.presentation.inventory_list.components.RecipeWizardBar
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onNavigateToAddInventory: () -> Unit,
    onNavigateToEditInventory: (Int) -> Unit,
    onNavigateToReceiptScan: () -> Unit,
    onNavigateToRecipes: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isFabExpanded by remember { mutableStateOf(false) }

    val selectedCount by remember(state.selectedIds) {
        derivedStateOf { state.selectedIds.size }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.handleEvent(InventoryListContract.Event.OnToggleCamera)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InventoryListContract.SideEffect.ShowSnackbar ->
                    scope.launch { snackbarHostState.showSnackbar(effect.message.asString(context)) }
                is InventoryListContract.SideEffect.NavigateToAddInventory -> onNavigateToAddInventory()
                is InventoryListContract.SideEffect.NavigateToEditInventory -> onNavigateToEditInventory(effect.itemId)
                is InventoryListContract.SideEffect.NavigateToRecipesWithFilters -> onNavigateToRecipes(effect.selectedIds)
                is InventoryListContract.SideEffect.NavigateToReceiptScan -> onNavigateToReceiptScan()
            }
        }
    }

    // Barkod kamera dialogu
    if (state.isCameraVisible) {
        Dialog(
            onDismissRequest = { viewModel.handleEvent(InventoryListContract.Event.OnToggleCamera) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(Modifier.fillMaxSize()) {
                CameraPreview(
                    onBarcodeScanned = { barcode ->
                        viewModel.handleEvent(InventoryListContract.Event.OnBarcodeDetected(barcode))
                    },
                    isLoading = state.isBarcodeLoading
                )
                IconButton(
                    onClick = { viewModel.handleEvent(InventoryListContract.Event.OnToggleCamera) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(RefoodioTheme.spacing.large)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }

    // Barkod ürün düzenleme bottom sheet
    val scannedItem = state.scannedItem
    if (scannedItem != null) {
        BarcodeResultBottomSheet(
            item = scannedItem,
            onNameChanged = { viewModel.handleEvent(InventoryListContract.Event.OnScannedItemNameChanged(it)) },
            onQuantityChanged = { viewModel.handleEvent(InventoryListContract.Event.OnScannedItemQuantityChanged(it)) },
            onUnitChanged = { viewModel.handleEvent(InventoryListContract.Event.OnScannedItemUnitChanged(it)) },
            onCategoryChanged = { viewModel.handleEvent(InventoryListContract.Event.OnScannedItemCategoryChanged(it)) },
            onConfirm = { viewModel.handleEvent(InventoryListContract.Event.OnConfirmBarcodeItem) },
            onDismiss = { viewModel.handleEvent(InventoryListContract.Event.OnDismissBarcodeItem) }
        )
    }

    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.handleEvent(InventoryListContract.Event.OnDeleteDismissed) },
            title = { Text("Ürünleri Sil") },
            text = { Text("${selectedCount} ürün envanterden kalıcı olarak silinecek. Onaylıyor musun?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.handleEvent(InventoryListContract.Event.DeleteInventory) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sil") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.handleEvent(InventoryListContract.Event.OnDeleteDismissed) }) {
                    Text("İptal")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ExpandableFab(
                expanded = isFabExpanded,
                onToggle = { isFabExpanded = !isFabExpanded },
                onManual = {
                    isFabExpanded = false
                    viewModel.handleEvent(InventoryListContract.Event.NavigateAddInventory)
                },
                onBarcode = {
                    isFabExpanded = false
                    val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (granted == PackageManager.PERMISSION_GRANTED) {
                        viewModel.handleEvent(InventoryListContract.Event.OnToggleCamera)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onReceipt = {
                    isFabExpanded = false
                    viewModel.handleEvent(InventoryListContract.Event.NavigateToReceiptScan)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            InventoryContent(state = state, onEvent = viewModel::handleEvent)

            RecipeWizardBar(
                selectedCount = selectedCount,
                onFindRecipesClick = { viewModel.handleEvent(InventoryListContract.Event.OnFindRecipesClick) },
                onClearSelection = { viewModel.handleEvent(InventoryListContract.Event.OnClearSelection) },
                onDeleteSelected = { viewModel.handleEvent(InventoryListContract.Event.OnRequestDelete) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ExpandableFab(
    expanded: Boolean,
    onToggle: () -> Unit,
    onManual: () -> Unit,
    onBarcode: () -> Unit,
    onReceipt: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "fab_rotation"
    )

    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FabOption(label = "Fiş Tara", icon = Icons.Default.Receipt, onClick = onReceipt)
                FabOption(label = "Barkod Oku", icon = Icons.Default.QrCodeScanner, onClick = onBarcode)
                FabOption(label = "Manuel Giriş", icon = Icons.Default.Edit, onClick = onManual)
            }
        }

        Spacer(Modifier.height(12.dp))

        FloatingActionButton(onClick = onToggle) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ekle",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarcodeResultBottomSheet(
    item: com.refoodio.core.domain.model.inventory.InventoryItem,
    onNameChanged: (String) -> Unit,
    onQuantityChanged: (Double) -> Unit,
    onUnitChanged: (FoodUnit) -> Unit,
    onCategoryChanged: (FoodCategory) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = RefoodioTheme.spacing.large)
                .padding(bottom = RefoodioTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.medium)
        ) {
            Text(
                text = "Ürün Bilgileri",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            HorizontalDivider()

            // Ürün adı
            OutlinedTextField(
                value = item.name,
                onValueChange = onNameChanged,
                label = { Text("Ürün Adı") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Miktar + Birim
            Row(horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)) {
                OutlinedTextField(
                    value = item.quantity.toString(),
                    onValueChange = { onQuantityChanged(it.toDoubleOrNull() ?: item.quantity) },
                    label = { Text("Miktar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                BarcodeUnitDropdown(
                    selected = item.unit,
                    onSelected = onUnitChanged,
                    modifier = Modifier.weight(1f)
                )
            }

            // Kategori
            BarcodeCategoryDropdown(
                selected = item.category,
                onSelected = onCategoryChanged,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(RefoodioTheme.spacing.small))

            // Butonlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text("İptal") }

                com.refoodio.core.ui.components.RefoodioPrimaryButton(
                    text = "Envantere Ekle",
                    onClick = onConfirm,
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarcodeUnitDropdown(
    selected: FoodUnit,
    onSelected: (FoodUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
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
                    onClick = { onSelected(unit); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarcodeCategoryDropdown(
    selected: FoodCategory,
    onSelected: (FoodCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
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
                    onClick = { onSelected(category); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun FabOption(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        SmallFloatingActionButton(onClick = onClick) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        }
    }
}
