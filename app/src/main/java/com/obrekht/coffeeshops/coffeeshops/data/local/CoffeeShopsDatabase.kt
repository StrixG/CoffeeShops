package com.obrekht.coffeeshops.coffeeshops.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.obrekht.coffeeshops.coffeeshops.data.model.CoffeeShopEntity

@Database(
    entities = [
        CoffeeShopEntity::class,
    ], version = 1
)
abstract class CoffeeShopsDatabase : RoomDatabase() {
    abstract fun coffeeShopsDao(): CoffeeShopsDao
}
