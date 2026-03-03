// com.refoodio.network.di.Qualifiers.kt
package com.refoodio.core.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FoodApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiApiRetrofit