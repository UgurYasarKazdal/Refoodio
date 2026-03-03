package com.refoodio.recipe.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.refoodio.core.domain.model.recipe.IngredientItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientSelectionSection(
    ingredients: List<IngredientItem>,
    onToggle: (IngredientItem) -> Unit
) {
    Column {
        Text(
            text = "Malzemelerini Seç",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        // Kategorilere göre gruplayarak gösteriyoruz
        val grouped = ingredients.groupBy { it.category }

        grouped.forEach { (category, items) ->
            Text(category, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { item ->
                    FilterChip(
                        selected = item.isSelected,
                        onClick = { onToggle(item) },
                        label = { Text(item.name) },
                        leadingIcon = if (item.isExpiredSoon) {
                            {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}