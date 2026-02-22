package com.refoodio.settings.presentation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Sayfa uzunsa kaydırılabilir olmalı
    ) {
        Text(
            text = "Ayarlar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 1. Grup: Bildirimler
        SettingsGroup(title = "Bildirim Ayarları") {
            SettingsSwitchItem(
                title = "Son Kullanma Uyarısı",
                description = "Ürünlerin tarihi geçmeden haber ver",
                icon = Icons.Default.Notifications,
                state = true
            )
            SettingsSwitchItem(
                title = "Haftalık Özet",
                description = "Envanter durumunu raporla",
                icon = Icons.Default.DateRange,
                state = false
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Grup: Görünüm
        SettingsGroup(title = "Görünüm") {
            SettingsClickableItem(
                title = "Tema",
                description = "Karanlık / Aydınlık Mod",
                icon = Icons.Default.BrightnessMedium
            )
            SettingsClickableItem(
                title = "Dil",
                description = "Türkçe",
                icon = Icons.Default.Language
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Grup: Veri ve Güvenlik
        SettingsGroup(title = "Veri") {
            SettingsClickableItem(
                title = "Verileri Yedekle",
                description = "Google Drive ile senkronize et",
                icon = Icons.Default.CloudUpload
            )
            SettingsClickableItem(
                title = "Tüm Verileri Sil",
                description = "Bu işlem geri alınamaz",
                icon = Icons.Default.Delete,
                contentColor = MaterialTheme.colorScheme.error
            )
        }
    }
}