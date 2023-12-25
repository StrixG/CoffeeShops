package com.obrekht.coffeeshops.menu.data.di

import com.obrekht.coffeeshops.menu.data.remote.MenuApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MenuServiceModule {

    @Singleton
    @Provides
    fun provideMenuService(retrofit: Retrofit): MenuApiService =
        retrofit.create()
}
