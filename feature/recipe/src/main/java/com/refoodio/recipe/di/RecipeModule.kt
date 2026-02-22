package com.refoodio.recipe.di

import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.recipe.navigation.RecipeNavImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RecipeModule {
    @Binds
    @IntoSet
    abstract fun bindHomeNav(impl: RecipeNavImpl): FeatureNavEntry

}