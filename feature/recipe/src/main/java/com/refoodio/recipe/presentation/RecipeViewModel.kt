package com.refoodio.recipe.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.refoodio.core.domain.model.recipe.IngredientItem
import com.refoodio.core.domain.use_case.recipe.RecipeUseCases
import com.refoodio.core.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class RecipeViewModel @Inject constructor(
    private val recipeUseCases: RecipeUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeWizardUiState())
    val uiState: StateFlow<RecipeWizardUiState> = _uiState.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            recipeUseCases.getInventoriesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val items = resource.data.map { domainModel ->
                            IngredientItem(
                                id = domainModel.id.toString(),
                                name = domainModel.name,
                                //TODO: Buraya kategoriler gelecek,
                                category = domainModel.name,
                                isExpiredSoon = domainModel.isNearExpiry(), // 3 günden az kalanlar
                                isSelected = domainModel.isNearExpiry()     // Otomatik seçim mantığı
                            )
                        }
                        _uiState.update {
                            it.copy(
                                ingredients = items, isLoading = false
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false, errorMessage = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun toggleIngredient(item: IngredientItem) {
        _uiState.update { currentState ->
            val updatedList = currentState.ingredients.map {
                if (it.id == item.id) it.copy(isSelected = !it.isSelected) else it
            }
            currentState.copy(ingredients = updatedList)
        }
    }

    fun generateRecipe() {
        val currentState = _uiState.value
        val selectedItems = currentState.ingredients.filter { it.isSelected }

        if (selectedItems.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Lütfen en az bir malzeme seçin.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Race Condition Önlemi: API'ye o anki seçimlerin "snapshot"ını gönderiyoruz.
            val result = recipeUseCases.generateRecipeUseCase(
                ingredients = selectedItems,
                method = currentState.selectedCookingMethod,
                maxTime = currentState.selectedTime,
                isGourmet = currentState.isGourmetMode
            )

            // Sonucu UI State'e işle
            _uiState.update { it.copy(isLoading = false, generatedRecipe = result.getOrNull()) }
        }
    }
}