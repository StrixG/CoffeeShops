package com.obrekht.coffeeshops.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.obrekht.coffeeshops.cart.data.local.CartDao
import com.obrekht.coffeeshops.cart.data.model.CartItemEntity
import com.obrekht.coffeeshops.coffeeshops.data.local.CoffeeShopsDao
import com.obrekht.coffeeshops.coffeeshops.data.model.CoffeeShopEntity
import com.obrekht.coffeeshops.menu.data.local.MenuDao
import com.obrekht.coffeeshops.menu.data.model.MenuItemEntity

@Database(
    entities = [
        CoffeeShopEntity::class,
        MenuItemEntity::class,
        CartItemEntity::class
    ], version = 1
)
abstract class CoffeeShopsDatabase : RoomDatabase() {
    abstract fun coffeeShopsDao(): CoffeeShopsDao
    abstract fun menuDao(): MenuDao
    abstract fun cartDao(): CartDao
}
