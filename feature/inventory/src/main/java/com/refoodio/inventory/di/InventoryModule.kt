package com.refoodio.inventory.di

import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.inventory.data.repository.InventoryRepositoryImpl
import com.refoodio.inventory.navigation.InventoryNavImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InventoryModule {
    @Binds
    @IntoSet
    abstract fun bindInventoryNav(impl: InventoryNavImpl): FeatureNavEntry

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepositoryImpl: InventoryRepositoryImpl
    ): InventoryRepository
}