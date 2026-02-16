package com.refoodio.inventory.presentation.add_inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.domain.util.toReadableDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryScreen(
    viewModel: InventoryAddViewModel = hiltViewModel(), onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InventoryAddContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

// DatePicker görünürlük kontrolü
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.expiryDate
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // İçeriğin kaydırılabilir olmasını sağlar
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Arama Çubuğu
        OutlinedTextField(
            value = state.searchQuery, // State içinde query tuttuğunu varsayıyoruz
            onValueChange = { viewModel.handleEvent(InventoryAddContract.Event.OnQueryChanged(it)) },
            label = { Text("Ürün Ara (örn: Yoğurt)") },
            modifier = Modifier.fillMaxWidth()
        )

        // 2. Öneri Listesi (Race Condition'ı ViewModel'de çözmüştük, burada sadece gösteriyoruz)
        if (state.suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
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
                            .padding(8.dp))
                }
            }
        }

        // 3. Seçilen Ürün Bilgileri
        if (state.selectedFoodName.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Seçilen: ${state.selectedFoodName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "Kategori: ${state.selectedCategory}")

                    // Miktar Butonları (+/-)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnDecrementQuantity) }) {
                            Icon(Icons.Default.RemoveCircle, "Azalt")
                        }
                        Text(text = "${state.quantity}")
                        IconButton(onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnIncrementQuantity) }) {
                            Icon(Icons.Default.AddCircle, "Artır")
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true } // Alana tıklayınca takvim açılsın
                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Son Tüketim Tarihi", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = state.expiryDate?.toReadableDate() ?: "Tarih Seçilmedi",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // DatePicker Dialog
        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.handleEvent(InventoryAddContract.Event.OnDateChanged(it))
                    }
                    showDatePicker = false
                }) { Text("Tamam") }
            }, dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("İptal") }
            }) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Kaydet Butonu
        Button(
            onClick = { viewModel.handleEvent(InventoryAddContract.Event.OnSaveProduct) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.selectedFoodName.isNotEmpty() && !state.isLoading
        ) {
            if (state.isLoading) {
                //TODO:  CircularProgressIndicator(size = 20.dp, color = Color.White)
            } else {
                Text("Envantere Ekle")
            }
        }
    }
}

