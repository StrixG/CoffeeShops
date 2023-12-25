package com.obrekht.coffeeshops.menu.data.di

import com.obrekht.coffeeshops.menu.data.repository.DefaultMenuRepository
import com.obrekht.coffeeshops.menu.data.repository.MenuRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MenuRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindMenuRepository(repository: DefaultMenuRepository): MenuRepository
}
