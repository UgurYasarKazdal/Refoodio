package com.refoodio.inventory.di

import com.refoodio.core.domain.repository.FoodCatalogRepository
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.catalog.GetFoodSuggestionsUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InsertProductUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.core.domain.use_case.inventory.addInventory.ValidateInventoryUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.DeleteProductUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.GetProductsUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.InventoryListUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class) // ViewModel'lerde kullanılacağı için
object InventoryUseCaseModule {

    @Provides
    fun provideValidateInventoryUseCase(): ValidateInventoryUseCase {
        return ValidateInventoryUseCase()
    }

    @Provides
    fun provideInventoryUseCases(
        inventoryRepository: InventoryRepository
    ): InventoryListUseCases {
        return InventoryListUseCases(
            getProducts = GetProductsUseCase(inventoryRepository),
            deleteProduct = DeleteProductUseCase(inventoryRepository)
        )
    }

    @Provides
    fun provideInventoryAddUseCases(
        inventoryRepository: InventoryRepository,
        catalogRepository: FoodCatalogRepository,
        validateInventory: ValidateInventoryUseCase
    ): InventoryAddUseCases = InventoryAddUseCases(
        insertProduct = InsertProductUseCase(inventoryRepository, validateInventory),
        suggestionsUseCase = GetFoodSuggestionsUseCase(catalogRepository)
    )
}