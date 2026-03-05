package com.refoodio.core.domain.use_case.recipe

import com.refoodio.core.domain.use_case.inventory.inventoryList.GetInventoriesUseCase

data class RecipeUseCases(
    val getInventoriesUseCase: GetInventoriesUseCase,
    val generateRecipeUseCase: GenerateRecipeUseCase
)