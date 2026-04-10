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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
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

    // Barkod ürün onay dialogu
    state.scannedItem?.let { item ->
        AlertDialog(
            onDismissRequest = { viewModel.handleEvent(InventoryListContract.Event.OnDismissBarcodeItem) },
            title = { Text("Ürün Bulundu") },
            text = { Text("\"${item.name}\" envanterine eklensin mi?") },
            confirmButton = {
                TextButton(onClick = { viewModel.handleEvent(InventoryListContract.Event.OnConfirmBarcodeItem) }) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.handleEvent(InventoryListContract.Event.OnDismissBarcodeItem) }) {
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
                onDeleteSelected = { viewModel.handleEvent(InventoryListContract.Event.DeleteInventory) },
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
