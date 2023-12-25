package com.obrekht.coffeeshops.app.data.di

import android.content.Context
import androidx.room.Room
import com.obrekht.coffeeshops.app.data.CoffeeShopsDatabase
import com.obrekht.coffeeshops.cart.data.local.CartDao
import com.obrekht.coffeeshops.coffeeshops.data.local.CoffeeShopsDao
import com.obrekht.coffeeshops.menu.data.local.MenuDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CoffeeShopsDatabase =
        Room.databaseBuilder(
            context,
            CoffeeShopsDatabase::class.java,
            "coffee-shops"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCoffeeShopsDao(database: CoffeeShopsDatabase): CoffeeShopsDao =
        database.coffeeShopsDao()

    @Provides
    fun provideMenuDao(database: CoffeeShopsDatabase): MenuDao =
        database.menuDao()

    @Provides
    fun provideCartDao(database: CoffeeShopsDatabase): CartDao =
        database.cartDao()
}
