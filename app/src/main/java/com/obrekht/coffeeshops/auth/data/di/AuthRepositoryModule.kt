package com.obrekht.coffeeshops.auth.data.di

import com.obrekht.coffeeshops.auth.data.repository.AuthRepository
import com.obrekht.coffeeshops.auth.data.repository.DefaultAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepository(repository: DefaultAuthRepository): AuthRepository
}
