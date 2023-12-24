package com.obrekht.coffeeshops.auth.data.di

import com.obrekht.coffeeshops.auth.data.remote.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthServiceModule {

    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthApiService =
        retrofit.create()
}
