package com.refoodio.recipe.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecipePreferencesSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Tarif Tercihleri", style = MaterialTheme.typography.titleMedium)

        // Pişirme Yöntemi
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Fırın", "Tencere", "Airfryer").forEach { method ->
                SuggestionChip(onClick = { /* TODO */ }, label = { Text(method) })
            }
        }

        // Stil Seçimi (Gurme vs İsraf Karşıtı)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gurme Modu (En uyumlu olanları seç)")
            Switch(checked = true, onCheckedChange = { /* TODO */ })
        }
    }
}