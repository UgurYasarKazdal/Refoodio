package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

// Silme onayı için durum
private sealed interface DeleteConfirmState {
    data object None : DeleteConfirmState
    data class SingleItem(val item: InventoryListContract.InventoryItemUiModel) : DeleteConfirmState
    data object AllItems : DeleteConfirmState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionBasketSheet(
    selectedItems: List<InventoryListContract.InventoryItemUiModel>,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onFindRecipes: () -> Unit,
    onBulkConsume: (amounts: Map<Int, Double>) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onEditItem: (Int) -> Unit,
    onDeselectItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Varsayılan = ürünün mevcut miktarı
    val amounts = remember(selectedItems) {
        mutableStateMapOf<Int, Double>().apply {
            selectedItems.forEach { put(it.id, it.originalItem.quantity) }
        }
    }

    val totalConsumeAmount = amounts.values.sumOf { it }
    val canConsume = totalConsumeAmount > 0.0

    var deleteConfirm by remember { mutableStateOf<DeleteConfirmState>(DeleteConfirmState.None) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // ── Başlık ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tezgah",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${selectedItems.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }

            HorizontalDivider()

            // ── Ürün listesi ───────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(selectedItems, key = { it.id }) { item ->
                    val currentAmount = amounts[item.id] ?: item.originalItem.quantity
                    val maxAmount = item.originalItem.quantity
                    val unitName = stringResource(item.unit.shortNameResId)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Ürün başlığı + işlem butonları
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = item.groupIcon),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // Düzenle
                                FilledTonalIconButton(
                                    onClick = { onEditItem(item.id) },
                                    modifier = Modifier.size(32.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Düzenle",
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // Envanterden sil
                                FilledTonalIconButton(
                                    onClick = { deleteConfirm = DeleteConfirmState.SingleItem(item) },
                                    modifier = Modifier.size(32.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Envanterden Sil",
                                        modifier = Modifier.size(15.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // Sadece seçimden çıkar
                                FilledTonalIconButton(
                                    onClick = { onDeselectItem(item.id) },
                                    modifier = Modifier.size(32.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Tezgahtan Çıkar",
                                        modifier = Modifier.size(15.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Stok bilgisi + stepper
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Stok: ${"%.1f".format(maxAmount)} $unitName",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )

                                // [-] stepper
                                FilledTonalIconButton(
                                    onClick = {
                                        amounts[item.id] = (currentAmount - item.unit.step)
                                            .coerceAtLeast(0.0)
                                    },
                                    modifier = Modifier.size(30.dp),
                                    enabled = currentAmount > 0.0
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Azalt",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                // Miktar text field
                                OutlinedTextField(
                                    value = if (currentAmount == 0.0) "" else "%.1f".format(currentAmount),
                                    onValueChange = { raw ->
                                        val parsed = raw.toDoubleOrNull()
                                        if (parsed != null) {
                                            amounts[item.id] = parsed.coerceIn(0.0, maxAmount)
                                        } else if (raw.isEmpty()) {
                                            amounts[item.id] = 0.0
                                        }
                                    },
                                    modifier = Modifier.width(80.dp),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        textAlign = TextAlign.Center,
                                        fontSize = 13.sp
                                    ),
                                    suffix = {
                                        Text(unitName, style = MaterialTheme.typography.labelSmall)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                // [+] stepper
                                FilledTonalIconButton(
                                    onClick = {
                                        amounts[item.id] = (currentAmount + item.unit.step)
                                            .coerceAtMost(maxAmount)
                                    },
                                    modifier = Modifier.size(30.dp),
                                    enabled = currentAmount < maxAmount
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Artır",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // ── Alt aksiyon butonları ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tüket (varsayılan miktar dolduğu için hemen aktif)
                Button(
                    onClick = { onBulkConsume(amounts.toMap()) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canConsume
                ) {
                    Text("Tüket")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tarif Bul
                    Button(
                        onClick = onFindRecipes,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tarif Bul")
                    }

                    // Tümünü Sil
                    OutlinedButton(
                        onClick = { deleteConfirm = DeleteConfirmState.AllItems },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hepsini Sil")
                    }
                }
            }
        } // Column sonu

        // ── Silme onay overlay ─────────────────────────────────────────────
        if (deleteConfirm !is DeleteConfirmState.None) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Uyarı ikonu
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Başlık
                        Text(
                            text = when (val s = deleteConfirm) {
                                is DeleteConfirmState.SingleItem -> "${s.item.name} silinsin mi?"
                                is DeleteConfirmState.AllItems   -> "${selectedItems.size} ürün silinsin mi?"
                                else -> ""
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // Açıklama
                        Text(
                            text = "Bu işlem geri alınamaz. Ürün envanterinden kalıcı olarak silinecek.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Sil butonu
                        Button(
                            onClick = {
                                when (val s = deleteConfirm) {
                                    is DeleteConfirmState.SingleItem -> onDeleteItem(s.item.id)
                                    is DeleteConfirmState.AllItems   -> onDelete()
                                    else -> Unit
                                }
                                deleteConfirm = DeleteConfirmState.None
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Evet, Sil")
                        }

                        // İptal
                        TextButton(
                            onClick = { deleteConfirm = DeleteConfirmState.None },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Vazgeç")
                        }
                    }
                }
            }
        } // if sonu

        } // outer Box sonu
    }
}
