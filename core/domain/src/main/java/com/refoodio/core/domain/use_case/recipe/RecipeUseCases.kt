package com.refoodio.core.domain.use_case.recipe

import com.refoodio.core.domain.use_case.inventory.inventoryList.GetProductsUseCase

data class RecipeUseCases(
    val getProductsUseCase: GetProductsUseCase,
    val generateRecipeUseCase: GenerateRecipeUseCase
)