package com.obrekht.coffeeshops.cart.data.di

import com.obrekht.coffeeshops.cart.data.repository.CartRepository
import com.obrekht.coffeeshops.cart.data.repository.DefaultCartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCartRepository(repository: DefaultCartRepository): CartRepository
}
