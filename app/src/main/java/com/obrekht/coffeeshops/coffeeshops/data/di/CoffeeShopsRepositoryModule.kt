package com.obrekht.coffeeshops.coffeeshops.data.di

import com.obrekht.coffeeshops.coffeeshops.data.repository.CoffeeShopsRepository
import com.obrekht.coffeeshops.coffeeshops.data.repository.DefaultCoffeeShopsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCoffeeShopsRepository(repository: DefaultCoffeeShopsRepository): CoffeeShopsRepository
}
