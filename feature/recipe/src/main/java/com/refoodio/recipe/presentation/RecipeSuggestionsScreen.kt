package com.refoodio.recipe.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.util.daysToMillis

@Composable
fun RecipeScreen(
    recipes: List<Recipe> = emptyList(), // ViewModel'den gelen tarif listesi
    inventoryItems: List<InventoryItem> = emptyList(), // Envanterdeki ürünler
    modifier: Modifier = Modifier
) {
    val sampleInventoryItems = listOf(
        InventoryItem(
            id = 1,
            name = "Süt",
            expiryDate = System.currentTimeMillis() + 1.daysToMillis, // Yarın bitiyor (Kritik!)
            quantity = 2.0,
            unit = FoodUnit.LITER
        ),
        InventoryItem(
            id = 2,
            name = "Yumurta",
            expiryDate = System.currentTimeMillis() + 10.daysToMillis, // Süresi var
            quantity = 12.0,
            unit = FoodUnit.PIECE
        ),
        InventoryItem(
            id = 3,
            name = "Tavuk Göğsü",
            expiryDate = System.currentTimeMillis() - 1.daysToMillis, // Tarihi geçmiş!
            quantity = 500.0,
            unit = FoodUnit.GRAM
        )
    )
    val sampleRecipes = listOf(
        Recipe(
            id = 1,
            name = "Menemen",
            description = "Klasik Türk kahvaltısı",
            requiredIngredients = listOf("Yumurta", "Domates", "Biber"),
            instructions = "Domatesleri soteleyin, yumurtaları ekleyin..."
        ),
        Recipe(
            id = 2,
            name = "Meyve Salatası",
            description = "Hafif tatlı",
            requiredIngredients = listOf("Elma", "Muz", "Portakal"),
            instructions = "Tüm meyveleri doğrayıp karıştırın."
        )
    )
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Malzemelerine Uygun Tarifler",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Son kullanma tarihi yaklaşanlara öncelik verilmiştir.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(sampleRecipes) { recipe ->
            RecipeCard(recipe = recipe, inventoryItems = sampleInventoryItems)
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, inventoryItems: List<InventoryItem>) {
    // Tarifteki malzemelerden hangilerinin tarihi geçmek üzere?
    val criticalIngredients = inventoryItems.filter { item ->
        recipe.requiredIngredients.contains(item.name) && item.isNearExpiry()
        // Örn: son 3 gün
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (criticalIngredients.isNotEmpty())
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (criticalIngredients.isNotEmpty())
            BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (criticalIngredients.isNotEmpty()) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Öncelikli Kullanım") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gereken Malzemeler:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            // Malzeme listesi ve durumları
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recipe.requiredIngredients.forEach { ingredient ->
                    val isCritical = criticalIngredients.any { it.name == ingredient }
                    AssistChip(
                        onClick = { },
                        label = { Text(ingredient) },
                        leadingIcon = {
                            if (isCritical) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}