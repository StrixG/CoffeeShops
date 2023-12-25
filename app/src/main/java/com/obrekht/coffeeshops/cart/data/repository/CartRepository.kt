package com.obrekht.coffeeshops.cart.data.repository

import com.obrekht.coffeeshops.cart.data.model.CartItemEntity
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getAllStream(): Flow<List<CartItemEntity>>
    fun getByIdsStream(idList: List<Long>): Flow<List<CartItemEntity>>

    suspend fun add(menuItemId: Long)
    suspend fun remove(menuItemId: Long)

    suspend fun deleteAll()
}
