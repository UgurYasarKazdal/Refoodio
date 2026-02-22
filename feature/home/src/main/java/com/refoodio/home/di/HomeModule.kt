package com.refoodio.home.di

import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.home.navigation.HomeNavImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class HomeModule {
    @Binds
    @IntoSet
    abstract fun bindHomeNav(impl: HomeNavImpl): FeatureNavEntry

}