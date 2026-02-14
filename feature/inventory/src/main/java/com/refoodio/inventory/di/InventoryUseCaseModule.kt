package com.refoodio.inventory.di

import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.domain.use_case.inventory.DeleteProductUseCase
import com.refoodio.core.domain.use_case.inventory.GetProductsUseCase
import com.refoodio.core.domain.use_case.inventory.InsertProductUseCase
import com.refoodio.core.domain.use_case.inventory.InventoryUseCases
import com.refoodio.core.domain.use_case.inventory.ValidateInventoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class) // ViewModel'lerde kullanılacağı için
object InventoryUseCaseModule {

    @Provides
    fun provideValidateInventoryUseCase(): ValidateInventoryUseCase {
        return ValidateInventoryUseCase()
    }

    @Provides
    fun provideInventoryUseCases(
        repository: InventoryRepository,
        validateInventory: ValidateInventoryUseCase
    ): InventoryUseCases {
        return InventoryUseCases(
            getProducts = GetProductsUseCase(repository),
            insertProduct = InsertProductUseCase(repository, validateInventory),
            deleteProduct = DeleteProductUseCase(repository)
        )
    }
}