package com.refoodio.settings.presentation

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.refoodio.core.domain.model.settings.ThemeMode
import java.io.File

private val cookingMethods =
    listOf("Tencere", "Fırın", "Airfryer", "Izgara", "Tavada", "Buharda", "Barbekü")
private val dietOptions = listOf(
    "Düşük Kalori", "Yüksek Protein", "Düşük Karbonhidrat", "Vejetaryen", "Vegan", "Glutensiz"
)
private val notifyDaysOptions = listOf(1, 2, 3, 5, 7)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onSnackbarShown()
        }
    }

    // CSV export — dosyayı yaz, paylaşım intent'i aç
    LaunchedEffect(uiState.exportCsvContent) {
        uiState.exportCsvContent?.let { csv ->
            val file = File(context.cacheDir, "refoodio_envanter.csv")
            file.writeText(csv)
            val uri: Uri = FileProvider.getUriForFile(
                context, "${context.packageName}.provider", file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Refoodio Envanterim")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Envanteri Paylaş"))
            viewModel.onExportHandled()
        }
    }

    // ── Diyaloglar ───────────────────────────────────────────────────────
    if (uiState.showThemeDialog) {
        ThemePickerDialog(
            current = uiState.themeMode,
            onSelect = { viewModel.setThemeMode(it) },
            onDismiss = { viewModel.dismissThemeDialog() })
    }

    if (uiState.showNotifyDaysDialog) {
        NotifyDaysDialog(
            current = uiState.notifyDaysBefore,
            onSelect = { viewModel.setNotifyDaysBefore(it) },
            onDismiss = { viewModel.dismissNotifyDaysDialog() })
    }

    if (uiState.showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearConfirmDialog() },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Tüm Envanteri Sil") },
            text = { Text("Bu işlem geri alınamaz. Tüm ürünler kalıcı olarak silinecek.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAllInventory() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Evet, Sil") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissClearConfirmDialog() }) {
                    Text("İptal")
                }
            })
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text("Ayarlar", fontWeight = FontWeight.Bold)
        })
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── 1. Bildirimler ────────────────────────────────────────────
            SettingsGroup(title = "Bildirimler") {
                SwitchItem(
                    title = "Son Kullanma Uyarısı",
                    description = "Tarih yaklaşan ürünler için bildirim gönder",
                    icon = Icons.Default.Notifications,
                    checked = uiState.expiryNotificationsEnabled,
                    onToggle = { viewModel.toggleExpiryNotifications() })
                if (uiState.expiryNotificationsEnabled) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ClickableItem(
                        title = "Kaç gün önce uyarılayım?",
                        description = "${uiState.notifyDaysBefore} gün önce",
                        icon = Icons.Default.DateRange,
                        onClick = { viewModel.showNotifyDaysDialog() })
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SwitchItem(
                    title = "Haftalık Özet",
                    description = "Her hafta envanter durumunu raporla",
                    icon = Icons.Default.DateRange,
                    checked = uiState.weeklySummaryEnabled,
                    onToggle = { viewModel.toggleWeeklySummary() })
            }

            // ── 2. Görünüm ────────────────────────────────────────────────
            SettingsGroup(title = "Görünüm") {
                ClickableItem(
                    title = "Tema",
                    description = uiState.themeMode.labelTr,
                    icon = Icons.Default.BrightnessMedium,
                    onClick = { viewModel.showThemeDialog() })
            }

            // ── 3. Tarif Varsayılanları ───────────────────────────────────
            SettingsGroup(title = "Tarif Varsayılanları") {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pişirme yöntemi
                    PreferenceLabel("Varsayılan Pişirme Yöntemi")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cookingMethods.forEach { method ->
                            FilterChip(
                                selected = uiState.defaultCookingMethod == method,
                                onClick = { viewModel.setDefaultCookingMethod(method) },
                                label = { Text(method) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    HorizontalDivider()

                    // Diyet tercihleri
                    PreferenceLabel("Kalıcı Diyet Tercihleri")
                    Text(
                        "Seçtiklerin her tarif oluşturmada otomatik uygulanır",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dietOptions.forEach { option ->
                            FilterChip(
                                selected = option in uiState.defaultDietOptions,
                                onClick = { viewModel.toggleDefaultDiet(option) },
                                label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }

                    HorizontalDivider()

                    // Gurme modu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            PreferenceLabel("Varsayılan Tarif Stili")
                            Text(
                                if (uiState.defaultGourmetMode) "✨ Gurme — En uyumlu kombinasyon"
                                else "♻️ Ekonomik — İsrafı önle",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.defaultGourmetMode,
                            onCheckedChange = { viewModel.toggleDefaultGourmetMode() },
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            // ── 4. Veri ───────────────────────────────────────────────────
            SettingsGroup(title = "Veri") {
                ClickableItem(
                    title = "Envanteri Dışa Aktar",
                    description = "CSV formatında paylaş (Excel, Drive...)",
                    icon = Icons.Default.Share,
                    onClick = { viewModel.exportCsv() })
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ClickableItem(
                    title = "Tüm Envanteri Sil",
                    description = "Tüm ürünleri kalıcı olarak temizle",
                    icon = Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.showClearConfirmDialog() })
            }

            // ── 5. Hakkında ───────────────────────────────────────────────
            SettingsGroup(title = "Hakkında") {
                InfoItem(title = "Uygulama Versiyonu", value = "1.0.0")
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                InfoItem(title = "Geliştirici", value = "Refoodio")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Diyaloglar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ThemePickerDialog(
    current: ThemeMode, onSelect: (ThemeMode) -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Tema") }, text = {
        Column {
            ThemeMode.entries.forEach { mode ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(mode) }
                    .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = current == mode, onClick = { onSelect(mode) })
                    Spacer(Modifier.width(8.dp))
                    Text(mode.labelTr, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }, confirmButton = {}, dismissButton = {
        TextButton(onClick = onDismiss) { Text("İptal") }
    })
}

@Composable
private fun NotifyDaysDialog(
    current: Int, onSelect: (Int) -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Kaç gün önce uyarılayım?") }, text = {
        Column {
            notifyDaysOptions.forEach { days ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(days) }
                    .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = current == days, onClick = { onSelect(days) })
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$days gün önce", style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }, confirmButton = {}, dismissButton = {
        TextButton(onClick = onDismiss) { Text("İptal") }
    })
}

// ─────────────────────────────────────────────────────────────────────────────
// Bileşenler
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun SwitchItem(
    title: String, description: String, icon: ImageVector, checked: Boolean, onToggle: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = { onToggle() })
        })
}

@Composable
private fun ClickableItem(
    title: String,
    description: String,
    icon: ImageVector,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(title, color = if (tint != Color.Unspecified) tint else Color.Unspecified)
        },
        supportingContent = { Text(description, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = tint) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null) })
}

@Composable
private fun InfoItem(title: String, value: String) {
    ListItem(headlineContent = { Text(title) }, trailingContent = {
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    })
}

@Composable
private fun PreferenceLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}
