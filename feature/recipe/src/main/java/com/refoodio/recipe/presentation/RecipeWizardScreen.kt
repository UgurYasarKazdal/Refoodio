package com.refoodio.recipe.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.refoodio.recipe.presentation.components.IngredientSelectionSection
import com.refoodio.recipe.presentation.components.RecipePreferencesSection
import com.refoodio.recipe.presentation.components.RecipeResultCard

@Composable
fun RecipeWizardScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            Button(
                onClick = { viewModel.generateRecipe() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
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
                } else {
                    Text("Tarif Oluştur ✨")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tarif Sihirbazı",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Malzemelerini seç, sana özel gurme tarifini hazırlayalım.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            item {
                IngredientSelectionSection(
                    ingredients = uiState.ingredients,
                    onToggle = { viewModel.toggleIngredient(it) }
                )
            }

            item { HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f)) }

            item { RecipePreferencesSection() }

            // Sonuç Kartı - Animasyonlu Gösterim
            item {
                AnimatedVisibility(
                    visible = uiState.generatedRecipe != null,
                    enter = fadeIn() + expandVertically()
                ) {
                    uiState.generatedRecipe?.let { recipe ->
                        RecipeResultCard(recipe = recipe)
                    }
                }
                // Alt kısımda butonun altında kalmaması için boşluk
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
