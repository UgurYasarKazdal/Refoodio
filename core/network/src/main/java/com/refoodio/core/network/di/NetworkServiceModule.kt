package com.refoodio.core.network.di

import com.refoodio.core.network.api.FoodApi
import com.refoodio.core.network.api.GeminiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    @Provides
    @Singleton
    fun provideFoodRetrofit(@FoodApiRetrofit retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiRetrofit(@GeminiApiRetrofit retrofit: Retrofit): GeminiApiService {
        return retrofit.create(GeminiApiService::class.java)
    }
}