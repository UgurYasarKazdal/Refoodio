package com.refoodio.recipe.presentation

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.refoodio.core.domain.model.recipe.IngredientItem
import com.refoodio.core.domain.use_case.recipe.RecipeUseCases
import com.refoodio.core.domain.util.Resource
import com.refoodio.core.navigation.NavigationRoutes
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
    private val savedStateHandle: SavedStateHandle,
    private val app: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeWizardUiState())
    val uiState: StateFlow<RecipeWizardUiState> = _uiState.asStateFlow()

    private val preselectedIds: Set<String> by lazy {
        runCatching { savedStateHandle.toRoute<NavigationRoutes.RecipeRoute>().selectedIds }
            .getOrNull()
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()
    }

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            recipeUseCases.getInventoriesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val items = resource.data.map { domainModel ->
                            val daysLeft = (domainModel.expiryDate - System.currentTimeMillis()) /
                                    (24 * 60 * 60 * 1000L)
                            IngredientItem(
                                id = domainModel.id.toString(),
                                name = domainModel.name,
                                category = app.getString(domainModel.category.nameResId),
                                isExpiredSoon = daysLeft <= 7,
                                isSelected = preselectedIds.isNotEmpty() &&
                                        domainModel.id.toString() in preselectedIds
                                        || (preselectedIds.isEmpty() && daysLeft <= 3)
                            )
                        }
                        _uiState.update { it.copy(ingredients = items, isLoading = false) }
                    }
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = resource.message)
                    }
                }
            }
        }
    }

    fun toggleIngredient(item: IngredientItem) {
        _uiState.update { state ->
            state.copy(
                ingredients = state.ingredients.map {
                    if (it.id == item.id) it.copy(isSelected = !it.isSelected) else it
                }
            )
        }
    }

    fun addFreeIngredient(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        _uiState.update { state ->
            if (trimmed in state.freeIngredients) state
            else state.copy(freeIngredients = state.freeIngredients + trimmed)
        }
    }

    fun removeFreeIngredient(name: String) {
        _uiState.update { it.copy(freeIngredients = it.freeIngredients - name) }
    }

    fun selectCookingMethod(method: String) {
        _uiState.update { it.copy(selectedCookingMethod = method) }
    }

    fun selectTime(minutes: Int) {
        _uiState.update { it.copy(selectedTime = minutes) }
    }

    fun toggleGourmetMode() {
        _uiState.update { it.copy(isGourmetMode = !it.isGourmetMode) }
    }

    fun selectDoneness(doneness: String) {
        _uiState.update { it.copy(selectedDoneness = doneness) }
    }

    fun toggleDietOption(option: String) {
        _uiState.update { state ->
            val updated = if (option in state.selectedDietOptions)
                state.selectedDietOptions - option
            else
                state.selectedDietOptions + option
            state.copy(selectedDietOptions = updated)
        }
    }

    fun dismissRecipeSheet() {
        _uiState.update { it.copy(showRecipeSheet = false) }
    }

    fun reopenRecipeSheet() {
        if (_uiState.value.generatedRecipe != null) {
            _uiState.update { it.copy(showRecipeSheet = true) }
        }
    }

    fun generateRecipe() {
        val state = _uiState.value
        val inventorySelected = state.ingredients.filter { it.isSelected }
        val freeItems = state.freeIngredients.map { name ->
            IngredientItem(
                id = "free_$name",
                name = name,
                category = "Diğer",
                isExpiredSoon = false,
                isSelected = true
            )
        }
        val allSelected = inventorySelected + freeItems

        if (allSelected.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Lütfen en az bir malzeme seçin.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = recipeUseCases.generateRecipeUseCase(
                ingredients = allSelected,
                method = state.selectedCookingMethod,
                maxTime = state.selectedTime,
                isGourmet = state.isGourmetMode,
                doneness = state.selectedDoneness,
                dietOptions = state.selectedDietOptions.toList()
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    generatedRecipe = result.getOrNull(),
                    showRecipeSheet = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }
}
