package com.refoodio.inventory.di

import com.refoodio.inventory.domain.repository.InventoryRepository
import com.refoodio.inventory.domain.use_case.DeleteProductUseCase
import com.refoodio.inventory.domain.use_case.GetProductsUseCase
import com.refoodio.inventory.domain.use_case.InsertProductUseCase
import com.refoodio.inventory.domain.use_case.InventoryUseCases
import com.refoodio.inventory.domain.use_case.ValidateProductUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Uygulama boyunca tek bir instance olsun
object AppModule {

    // 2. ValidateProductUseCase için bir provider metodu ekleyin
    // Bu sınıfın başka bir bağımlılığı olmadığı için basitçe oluşturup döndürüyoruz.
    @Provides
    @Singleton
    fun provideValidateProductUseCase(): ValidateProductUseCase {
        return ValidateProductUseCase()
    }

    @Provides
    @Singleton
    fun provideInventoryUseCases(
        repository: InventoryRepository,
        validateProduct: ValidateProductUseCase
    ): InventoryUseCases {
        return InventoryUseCases(
            getProducts = GetProductsUseCase(repository),
            insertProduct = InsertProductUseCase(repository, validateProduct),
            deleteProduct = DeleteProductUseCase(repository)
        )
    }
}