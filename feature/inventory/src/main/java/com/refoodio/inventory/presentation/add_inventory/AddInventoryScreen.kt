package com.refoodio.inventory.presentation.add_inventory

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.ui.components.RefoodioPrimaryButton
import com.refoodio.core.ui.components.camera.CameraPreview
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R
import com.refoodio.inventory.presentation.add_inventory.components.ExpiryDateSection
import com.refoodio.inventory.presentation.add_inventory.components.SearchAndBarcodeField
import com.refoodio.inventory.presentation.add_inventory.components.SelectedInventoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryScreen(
    viewModel: InventoryAddViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val focusManager = LocalFocusManager.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.handleEvent(InventoryAddContract.Event.OnToggleCamera)
        } else {
            viewModel.handleEvent(InventoryAddContract.Event.OnPermissionDenied)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InventoryAddContract.SideEffect.NavigateBack -> onNavigateBack()
                is InventoryAddContract.SideEffect.ShowSnackBar -> {
                    val message = effect.message.asString(context)
                    snackbarHostState.showSnackbar(
                        message = message, duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.form.expiryDate
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Ürünü Düzenle" else "Ürün Ekle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(RefoodioTheme.spacing.large)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.large)
    ) {
        SearchAndBarcodeField(
            searchQuery = state.searchQuery,
            onQueryChange = { viewModel.handleEvent(InventoryAddContract.Event.OnQueryChanged(it)) }
        )

        if (state.isCameraVisible) {
            Dialog(
                onDismissRequest = { viewModel.handleEvent(InventoryAddContract.Event.OnToggleCamera) },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        onBarcodeScanned = { barcode ->
                            viewModel.handleEvent(
                                InventoryAddContract.Event.OnBarcodeScanned(
                                    barcode
                                )
                            )
                        }, isLoading = state.isLoading
                    )

                    IconButton(
                        onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnToggleCamera) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(RefoodioTheme.spacing.large)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.add_inventory_close_content_desc),
                            tint = Color.White
                        )
                    }
                }
            }

        }

        if (state.suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = RefoodioTheme.dimens.suggestionListMaxHeight)
            ) {
                items(state.suggestions) { food ->
                    Text(text = food.name, modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            focusManager.clearFocus()

                            viewModel.handleEvent(
                                InventoryAddContract.Event.OnSuggestionSelected(
                                    food
                                )
                            )
                        }
                        .padding(RefoodioTheme.spacing.medium))
                }
            }
        }

        AnimatedVisibility(
            visible = state.form.selectedFoodName.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.large)) {
                SelectedInventoryCard(
                    selectedFoodName = state.form.selectedFoodName,
                    selectedCategory = state.form.selectedCategory,
                    quantity = state.form.quantity,
                    onIncrement = { viewModel.handleEvent(InventoryAddContract.Event.OnIncrementQuantity) },
                    onDecrement = { viewModel.handleEvent(InventoryAddContract.Event.OnDecrementQuantity) },
                    selectedUnit = state.form.unit,
                    onUnitSelected = {
                        viewModel.handleEvent(
                            InventoryAddContract.Event.OnUnitSelected(
                                it
                            )
                        )
                    },
                    onQuantitySelected = {
                        viewModel.handleEvent(
                            InventoryAddContract.Event.OnQuantitySelected(
                                it
                            )
                        )
                    },
                )
                ExpiryDateSection(
                    expiryDate = state.form.expiryDate, onDateClick = { showDatePicker = true })
            }
        }



        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.handleEvent(
                            InventoryAddContract.Event.OnDateChanged(
                                it
                            )
                        )
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.add_inventory_dialog_ok)) }
            }, dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) { Text(stringResource(R.string.add_inventory_dialog_cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(RefoodioTheme.spacing.extraLarge))

        RefoodioPrimaryButton(
            text = if (state.isEditMode) "Değişiklikleri Kaydet"
                   else stringResource(R.string.add_inventory_save_button),
            onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnSaveInventory) },
            isLoading = state.isLoading,
            isEnabled = state.form.selectedFoodName.isNotEmpty()
        )
    }
    } // Scaffold
}