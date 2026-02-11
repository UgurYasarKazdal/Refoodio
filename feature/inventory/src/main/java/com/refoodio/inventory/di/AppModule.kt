package com.refoodio.inventory.di

import com.refoodio.inventory.domain.repository.InventoryRepository
import com.refoodio.inventory.domain.use_case.DeleteProductUseCase
import com.refoodio.inventory.domain.use_case.GetProductsUseCase
import com.refoodio.inventory.domain.use_case.InsertProductUseCase
import com.refoodio.inventory.domain.use_case.InventoryUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Uygulama boyunca tek bir instance olsun
object AppModule {
    @Provides
    @Singleton
    fun provideInventoryUseCases(repository: InventoryRepository): InventoryUseCases {
        return InventoryUseCases(
            getProducts = GetProductsUseCase(repository),
            insertProduct = InsertProductUseCase(repository),
            deleteProduct = DeleteProductUseCase(repository)
        )
    }
}