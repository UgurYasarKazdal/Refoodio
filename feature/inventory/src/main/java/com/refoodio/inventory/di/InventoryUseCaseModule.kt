package com.refoodio.inventory.di

import com.refoodio.core.domain.repository.FoodCatalogRepository
import com.refoodio.core.domain.repository.FoodRepository
import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.catalog.GetFoodSuggestionsUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.GetFoodByBarcodeUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InsertInventoryUseCase
import com.refoodio.core.domain.use_case.inventory.addInventory.InventoryAddUseCases
import com.refoodio.core.domain.use_case.inventory.addInventory.ValidateInventoryUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.DeleteInventoryUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.DeleteSelectedInventoriesUseCase
import com.refoodio.core.domain.use_case.inventory.inventoryList.GetInventoriesUseCase
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
            getInventories = GetInventoriesUseCase(inventoryRepository),
            deleteInventory = DeleteInventoryUseCase(inventoryRepository),
            deleteSelectedInventories = DeleteSelectedInventoriesUseCase(inventoryRepository)
        )
    }

    @Provides
    fun provideInventoryAddUseCases(
        inventoryRepository: InventoryRepository,
        catalogRepository: FoodCatalogRepository,
        foodRepository: FoodRepository,
        validateInventory: ValidateInventoryUseCase
    ): InventoryAddUseCases = InventoryAddUseCases(
        insertInventory = InsertInventoryUseCase(inventoryRepository, validateInventory),
        suggestionsUseCase = GetFoodSuggestionsUseCase(catalogRepository),
        getFoodByBarcodeUseCase = GetFoodByBarcodeUseCase(foodRepository)
    )
}