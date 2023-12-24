package com.obrekht.coffeeshops.coffeeshops.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.obrekht.coffeeshops.coffeeshops.data.model.CoffeeShopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeShopsDao {

    @Query("SELECT * FROM coffee_shop ORDER BY id ASC")
    fun observeAll(): Flow<List<CoffeeShopEntity>>

    @Query("SELECT * FROM coffee_shop WHERE id = :id")
    suspend fun getById(id: Long): CoffeeShopEntity?

    @Upsert
    suspend fun upsert(coffeeShop: CoffeeShopEntity)

    @Upsert
    suspend fun upsert(coffeeShopList: List<CoffeeShopEntity>)

    @Query("DELETE FROM coffee_shop WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM coffee_shop")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(coffeeShopList: List<CoffeeShopEntity>) {
        deleteAll()
        upsert(coffeeShopList)
    }
}
