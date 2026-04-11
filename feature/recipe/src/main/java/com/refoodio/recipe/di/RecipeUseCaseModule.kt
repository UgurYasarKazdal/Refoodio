package com.refoodio.recipe.di

import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.repository.SettingsRepository
import com.refoodio.core.domain.repository.recipe.RecipeRepository
import com.refoodio.core.domain.use_case.inventory.inventoryList.GetInventoriesUseCase
import com.refoodio.core.domain.use_case.recipe.GenerateRecipeUseCase
import com.refoodio.core.domain.use_case.recipe.RecipeUseCases
import com.refoodio.core.domain.use_case.settings.GetSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RecipeUseCaseModule {
    @Provides
    fun provideRecipeUseCasesModule(
        recipeRepository: RecipeRepository,
        inventoryRepository: InventoryRepository,
        settingsRepository: SettingsRepository
    ): RecipeUseCases {
        return RecipeUseCases(
            getInventoriesUseCase = GetInventoriesUseCase(inventoryRepository),
            generateRecipeUseCase = GenerateRecipeUseCase(recipeRepository),
            getSettingsUseCase = GetSettingsUseCase(settingsRepository)
        )
    }
}