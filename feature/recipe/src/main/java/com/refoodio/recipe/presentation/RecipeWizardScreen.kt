package com.refoodio.recipe.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.refoodio.core.domain.model.recipe.IngredientItem
import com.refoodio.recipe.presentation.components.RecipePreferencesContent
import com.refoodio.recipe.presentation.components.RecipeResultCard
import com.refoodio.recipe.presentation.components.cookingMethods

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeWizardScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
    showBackButton: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val recipeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val prefsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPreferencesSheet by remember { mutableStateOf(false) }

    // ── Tarif Sonucu Sheet ────────────────────────────────────────────────
    if (uiState.showRecipeSheet) {
        val recipe = uiState.generatedRecipe
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissRecipeSheet() },
            sheetState = recipeSheetState
        ) {
            if (recipe != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Başlık + kapat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tarif",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.dismissRecipeSheet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat")
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Scroll olan tarif içeriği
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp, bottom = 16.dp)
                    ) {
                        RecipeResultCard(recipe = recipe)
                    }

                    // Sabit buton — altta
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Button(
                        onClick = {
                            viewModel.dismissRecipeSheet()
                            viewModel.generateRecipe()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding()
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Başka Tarif Oluştur")
                    }
                }
            }
        }
    }

    // ── Tercihler Sheet ───────────────────────────────────────────────────
    if (showPreferencesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPreferencesSheet = false },
            sheetState = prefsSheetState
        ) {
            // Başlık + kapat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tarif Tercihleri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showPreferencesSheet = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                RecipePreferencesContent(
                    selectedMethod = uiState.selectedCookingMethod,
                    selectedTime = uiState.selectedTime,
                    isGourmetMode = uiState.isGourmetMode,
                    selectedDoneness = uiState.selectedDoneness,
                    selectedDietOptions = uiState.selectedDietOptions,
                    onMethodChange = { viewModel.selectCookingMethod(it) },
                    onTimeChange = { viewModel.selectTime(it) },
                    onGourmetToggle = { viewModel.toggleGourmetMode() },
                    onDonenessChange = { viewModel.selectDoneness(it) },
                    onDietToggle = { viewModel.toggleDietOption(it) }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tarif Sihirbazı", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                        }
                    }
                },
                actions = {
                    if (uiState.selectedCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                "${uiState.selectedCount}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // ── Tercihler özet satırı ─────────────────────────────────
                PreferencesSummaryBar(
                    selectedMethod = uiState.selectedCookingMethod,
                    selectedTime = uiState.selectedTime,
                    selectedDoneness = uiState.selectedDoneness,
                    isGourmetMode = uiState.isGourmetMode,
                    dietCount = uiState.selectedDietOptions.size,
                    onClick = { showPreferencesSheet = true }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    // "Tarifimi Gör" — tarif üretildi ama sheet kapalıysa göster
                    AnimatedVisibility(
                        visible = uiState.generatedRecipe != null && !uiState.showRecipeSheet && !uiState.isLoading,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.reopenRecipeSheet() },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tarifimi Gör ✨")
                        }
                    }

                    // Az malzeme uyarısı
                    AnimatedVisibility(
                        visible = uiState.selectedCount in 1..2,
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        Text(
                            "💡 Daha fazla malzeme seçersen daha iyi tarif alırsın",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    // Ana buton
                    Button(
                        onClick = { viewModel.generateRecipe() },
                        enabled = !uiState.isLoading && uiState.selectedCount > 0,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Şef Düşünüyor...")
                        } else if (uiState.selectedCount == 0) {
                            Text("Malzeme Seç")
                        } else {
                            Icon(Icons.Default.AutoAwesome, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tarif Oluştur (${uiState.selectedCount} malzeme)")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            // ── Seçilen malzemeler (envanterdekiler) ──────────────────────
            item {
                SelectedInventoryItemsSection(
                    urgentItems = uiState.selectedUrgentItems,
                    regularItems = uiState.selectedRegularItems,
                    onRemove = { viewModel.toggleIngredient(it) },
                    onAddMore = onNavigateBack
                )
            }

            // ── Ekstra malzeme girişi ─────────────────────────────────────
            item {
                FreeIngredientInput(
                    freeIngredients = uiState.freeIngredients,
                    onAdd = { viewModel.addFreeIngredient(it) },
                    onRemove = { viewModel.removeFreeIngredient(it) }
                )
            }

            // ── Hata mesajı ───────────────────────────────────────────────
            if (uiState.errorMessage != null) {
                item {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Seçilen envanter malzemeleri
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectedInventoryItemsSection(
    urgentItems: List<IngredientItem>,
    regularItems: List<IngredientItem>,
    onRemove: (IngredientItem) -> Unit,
    onAddMore: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Malzemeler",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(
                onClick = onAddMore,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 8.dp, vertical = 4.dp
                )
            ) {
                Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Malzeme Ekle", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (urgentItems.isEmpty() && regularItems.isEmpty()) {
            // Boş durum
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Envanterdeki malzemeleri henüz seçmediniz. Yukarıdaki ← ok ile geri dönüp malzeme seçebilir veya aşağıdan ekstra malzeme ekleyebilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // ⚠️ Önce bunları kullan — yakın tarihli malzemeler
            if (urgentItems.isNotEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "Önce Bunları Kullan",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            urgentItems.forEach { item ->
                                IngredientChip(
                                    item = item,
                                    isUrgent = true,
                                    onRemove = { onRemove(item) }
                                )
                            }
                        }
                    }
                }
            }

            // Normal malzemeler
            if (regularItems.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    regularItems.forEach { item ->
                        IngredientChip(
                            item = item,
                            isUrgent = false,
                            onRemove = { onRemove(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientChip(
    item: IngredientItem,
    isUrgent: Boolean,
    onRemove: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = {},
        label = { Text(item.name) },
        trailingIcon = {
            IconButton(onClick = onRemove, modifier = Modifier.size(18.dp)) {
                Icon(Icons.Default.Close, null, Modifier.size(14.dp))
            }
        },
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = if (isUrgent)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
            else
                MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = if (isUrgent)
                MaterialTheme.colorScheme.onErrorContainer
            else
                MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Ekstra serbest malzeme girişi
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FreeIngredientInput(
    freeIngredients: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Ekstra Malzeme",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Listede olmayan malzemeleri buraya ekle (tuz, yağ, baharat...)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (freeIngredients.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                freeIngredients.forEach { ingredient ->
                    InputChip(
                        selected = true,
                        onClick = {},
                        label = { Text(ingredient) },
                        trailingIcon = {
                            IconButton(
                                onClick = { onRemove(ingredient) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                            }
                        }
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Tuz, karabiber, sarımsak...") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (text.isNotBlank()) { onAdd(text); text = "" }
                })
            )
            IconButton(onClick = {
                if (text.isNotBlank()) { onAdd(text); text = "" }
            }) {
                Icon(Icons.Default.Add, "Ekle")
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tercihler özet satırı (bottom bar)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PreferencesSummaryBar(
    selectedMethod: String,
    selectedTime: Int,
    selectedDoneness: String,
    isGourmetMode: Boolean,
    dietCount: Int,
    onClick: () -> Unit
) {
    val methodEmoji = cookingMethods.find { it.label == selectedMethod }?.emoji ?: ""
    val styleSummary = if (isGourmetMode) "Gurme ✨" else "Ekonomik"
    val summaryParts = buildList {
        add("$methodEmoji $selectedMethod")
        add("${selectedTime} dk")
        add(selectedDoneness)
        if (dietCount > 0) add("$dietCount diyet")
        add(styleSummary)
    }

    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Tune, "Tercihler",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    summaryParts.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight, null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
