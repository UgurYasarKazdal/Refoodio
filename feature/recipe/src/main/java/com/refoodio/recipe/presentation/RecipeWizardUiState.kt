package com.refoodio.recipe.presentation

import com.refoodio.core.domain.model.recipe.IngredientItem
import com.refoodio.core.domain.model.recipe.Recipe

data class RecipeWizardUiState(
    val isLoading: Boolean = false,
    val ingredients: List<IngredientItem> = emptyList(),
    val freeIngredients: List<String> = emptyList(),
    val selectedCookingMethod: String = "Tencere",
    val selectedTime: Int = 30,
    val isGourmetMode: Boolean = false,
    val selectedDoneness: String = "Normal",
    val selectedDietOptions: Set<String> = emptySet(),
    val showRecipeSheet: Boolean = false,
    val generatedRecipe: Recipe? = null,
    val errorMessage: String? = null
) {
    val selectedCount: Int
        get() = ingredients.count { it.isSelected } + freeIngredients.size

    /** Envanterde seçilmiş malzemeler — tarif ekranında chip olarak gösterilir */
    val selectedInventoryItems: List<IngredientItem>
        get() = ingredients.filter { it.isSelected }

    val selectedUrgentItems: List<IngredientItem>
        get() = selectedInventoryItems.filter { it.isExpiredSoon }

    val selectedRegularItems: List<IngredientItem>
        get() = selectedInventoryItems.filter { !it.isExpiredSoon }

    // Aşağıdakiler artık kullanılmıyor (ekran tüm listeyi göstermediği için)
    // ama ileride ihtiyaç duyulursa burada.
    val urgentIngredients: List<IngredientItem>
        get() = ingredients.filter { it.isExpiredSoon }

    val regularIngredients: List<IngredientItem>
        get() = ingredients.filter { !it.isExpiredSoon }
}
