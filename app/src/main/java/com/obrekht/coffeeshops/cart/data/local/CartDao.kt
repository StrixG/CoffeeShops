package com.obrekht.coffeeshops.cart.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.obrekht.coffeeshops.cart.data.model.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_item")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_item WHERE menuItemId IN (:idList)")
    fun observeByIds(idList: List<Long>): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_item WHERE menuItemId = :menuItemId")
    suspend fun getById(menuItemId: Long): CartItemEntity?

    @Insert
    suspend fun insert(cartItem: CartItemEntity)

    @Query("UPDATE cart_item SET count = count + 1 WHERE menuItemId = :menuItemId")
    suspend fun add(menuItemId: Long)

    @Query("UPDATE cart_item SET count = count - 1 WHERE menuItemId = :menuItemId")
    suspend fun remove(menuItemId: Long)

    @Query("DELETE FROM cart_item WHERE menuItemId = :menuItemId")
    suspend fun deleteById(menuItemId: Long)

    @Query("DELETE FROM cart_item")
    suspend fun deleteAll()
}
