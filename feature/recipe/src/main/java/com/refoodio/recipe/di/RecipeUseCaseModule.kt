package com.refoodio.recipe.di

import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.repository.recipe.RecipeRepository
import com.refoodio.core.domain.use_case.inventory.inventoryList.GetProductsUseCase
import com.refoodio.core.domain.use_case.recipe.GenerateRecipeUseCase
import com.refoodio.core.domain.use_case.recipe.RecipeUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RecipeUseCaseModule {
    @Provides
    fun provideRecipeUseCasesModule(
        recipeRepository: RecipeRepository, inventoryRepository: InventoryRepository
    ): RecipeUseCases {
        return RecipeUseCases(
            getProductsUseCase = GetProductsUseCase(inventoryRepository),
            generateRecipeUseCase = GenerateRecipeUseCase(recipeRepository)
        )
    }
}