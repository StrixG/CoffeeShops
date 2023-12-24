package com.obrekht.coffeeshops.coffeeshops.data.di

import android.content.Context
import androidx.room.Room
import com.obrekht.coffeeshops.coffeeshops.data.local.CoffeeShopsDao
import com.obrekht.coffeeshops.coffeeshops.data.local.CoffeeShopsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoffeeShopsDatabaseModule {

    @Singleton
    @Provides
    fun provideCoffeeShopsDatabase(@ApplicationContext context: Context): CoffeeShopsDatabase =
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
}
