package com.obrekht.coffeeshops.coffeeshops.data.di

import com.obrekht.coffeeshops.coffeeshops.data.remote.CoffeeShopsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoffeeShopsServiceModule {

    @Singleton
    @Provides
    fun provideCoffeeShopsService(retrofit: Retrofit): CoffeeShopsApiService =
        retrofit.create()
}
