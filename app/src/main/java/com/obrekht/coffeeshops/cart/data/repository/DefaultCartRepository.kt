package com.obrekht.coffeeshops.cart.data.repository

import androidx.room.withTransaction
import com.obrekht.coffeeshops.app.data.CoffeeShopsDatabase
import com.obrekht.coffeeshops.cart.data.local.CartDao
import com.obrekht.coffeeshops.cart.data.model.CartItemEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultCartRepository @Inject constructor(
    private val database: CoffeeShopsDatabase,
    private val cartDao: CartDao
) : CartRepository {

    override fun getAllStream(): Flow<List<CartItemEntity>> = cartDao.observeAll()

    override fun getByIdsStream(idList: List<Long>): Flow<List<CartItemEntity>> =
        cartDao.observeByIds(idList)

    override suspend fun add(menuItemId: Long) {
        database.withTransaction {
            cartDao.getById(menuItemId)?.let {
                cartDao.add(menuItemId)
            } ?: run {
                cartDao.insert(CartItemEntity(menuItemId, 1))
            }
        }
    }

    override suspend fun remove(menuItemId: Long) {
        database.withTransaction {
            cartDao.getById(menuItemId)?.let {
                if (it.count == 1) {
                    cartDao.deleteById(menuItemId)
                } else {
                    cartDao.remove(menuItemId)
                }
            }
        }
    }

    override suspend fun deleteAll() = cartDao.deleteAll()
}
