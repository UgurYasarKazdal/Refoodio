package com.refoodio.home.di

import com.refoodio.core.domain.repository.InventoryRepository
import com.refoodio.home.domain.GetHomeStatsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object HomeUseCaseModule {

    @Provides
    fun provideGetHomeStatsUseCase(
        repository: InventoryRepository
    ): GetHomeStatsUseCase = GetHomeStatsUseCase(repository)
}
