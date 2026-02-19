package com.refoodio.inventory.presentation.add_inventory

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.domain.util.toReadableDate
import com.refoodio.core.ui.components.camera.CameraPreview
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryScreen(
    viewModel: InventoryAddViewModel = hiltViewModel(), onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }


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
        initialSelectedDateMillis = state.expiryDate
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(RefoodioTheme.spacing.large)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.large)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.handleEvent(InventoryAddContract.Event.OnQueryChanged(it)) },
            label = { Text(stringResource(R.string.add_inventory_search_label)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        viewModel.handleEvent(InventoryAddContract.Event.OnToggleCamera)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner, contentDescription = stringResource(
                            R.string.add_inventory_barcode_content_desc
                        )
                    )
                }
            })

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
                            Icons.Default.Close, contentDescription = stringResource(R.string.add_inventory_close_content_desc), tint = Color.White
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
                    Text(
                        text = food.name, modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
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

        if (state.selectedFoodName.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(RefoodioTheme.spacing.large)) {
                    Text(
                        text = stringResource(R.string.add_inventory_selected_label, state.selectedFoodName),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(text = stringResource(R.string.add_inventory_category_label, state.selectedCategory))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnDecrementQuantity) }) {
                            Icon(Icons.Default.RemoveCircle, stringResource(R.string.add_inventory_decrease_qty))
                        }
                        Text(text = "${state.quantity}")
                        IconButton(onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnIncrementQuantity) }) {
                            Icon(Icons.Default.AddCircle, stringResource(R.string.add_inventory_increase_qty))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .padding(vertical = RefoodioTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
            Spacer(modifier = Modifier.width(RefoodioTheme.spacing.smallMedium))
            Column {
                Text(
                    text = stringResource(R.string.add_inventory_expiry_date_label), style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = state.expiryDate?.toReadableDate() ?: stringResource(R.string.add_inventory_no_date_selected),
                    style = MaterialTheme.typography.bodyLarge
                )
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
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.add_inventory_dialog_cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(RefoodioTheme.spacing.extraLarge))

        Button(
            onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnSaveProduct) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.selectedFoodName.isNotEmpty() && !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(RefoodioTheme.dimens.loadingIndicatorSmall),
                    strokeWidth = RefoodioTheme.stroke.standard,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.add_inventory_save_button))
            }
        }
    }
}

