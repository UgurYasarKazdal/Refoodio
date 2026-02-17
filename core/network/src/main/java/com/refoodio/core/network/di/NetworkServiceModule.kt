package com.refoodio.core.network.di

import com.refoodio.core.network.api.FoodApi
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
    fun provideFoodApi(retrofit: Retrofit): FoodApi {
        // Hilt, 'retrofit' nesnesini core:network modülündeki NetworkModule'den bulup getirecek.
        // Biz de o nesneyi kullanarak FoodApi'yi oluşturuyoruz.
        return retrofit.create(FoodApi::class.java)
    }
}