package com.refoodio.core.data.di

import com.refoodio.core.data.repository.catalog.FoodCatalogRepositoryImpl
import com.refoodio.core.data.repository.inventory.FoodRepositoryImpl
import com.refoodio.core.data.repository.inventory.InventoryRepositoryImpl
import com.refoodio.core.data.repository.receipt.ReceiptRepositoryImpl
import com.refoodio.core.data.repository.recipe.RecipeRepositoryImpl
import com.refoodio.core.domain.repository.FoodCatalogRepository
import com.refoodio.core.domain.repository.FoodRepository
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.repository.ReceiptRepository
import com.refoodio.core.domain.repository.recipe.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepositoryImpl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindFoodCatalogRepository(
        impl: FoodCatalogRepositoryImpl
    ): FoodCatalogRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        foodRepositoryImpl: FoodRepositoryImpl
    ): FoodRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindReceiptRepository(
        receiptRepositoryImpl: ReceiptRepositoryImpl
    ): ReceiptRepository
}