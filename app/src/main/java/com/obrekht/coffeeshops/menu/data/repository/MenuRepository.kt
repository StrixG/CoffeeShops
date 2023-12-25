package com.obrekht.coffeeshops.menu.data.repository

import com.obrekht.coffeeshops.menu.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    suspend fun refreshById(coffeeShopId: Long)

    fun getByCoffeeShopIdStream(coffeeShopId: Long): Flow<List<MenuItem>>
    fun getByIdsStream(idList: List<Long>): Flow<List<MenuItem>>
    suspend fun getMenuItemById(id: Long): MenuItem?
}
