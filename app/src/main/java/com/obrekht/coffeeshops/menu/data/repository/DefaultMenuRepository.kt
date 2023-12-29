package com.obrekht.coffeeshops.menu.data.repository

import com.obrekht.coffeeshops.auth.data.model.exception.InvalidTokenException
import com.obrekht.coffeeshops.auth.data.utils.isAuthTokenValid
import com.obrekht.coffeeshops.core.data.model.EmptyBodyException
import com.obrekht.coffeeshops.menu.data.local.MenuDao
import com.obrekht.coffeeshops.menu.data.model.MenuItem
import com.obrekht.coffeeshops.menu.data.model.toEntity
import com.obrekht.coffeeshops.menu.data.model.toModel
import com.obrekht.coffeeshops.menu.data.remote.MenuApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class DefaultMenuRepository @Inject constructor(
    private val menuDao: MenuDao,
    private val menuApiService: MenuApiService
) : MenuRepository {

    override suspend fun refreshById(coffeeShopId: Long) {
        val response = menuApiService.getById(coffeeShopId)
        if (!response.isSuccessful) {
            if (!response.isAuthTokenValid) {
                throw InvalidTokenException()
            } else {
                throw HttpException(response)
            }
        }

        val body = response.body() ?: throw EmptyBodyException()
        menuDao.replaceAll(body.toEntity(coffeeShopId))
    }

    override fun getByCoffeeShopIdStream(coffeeShopId: Long): Flow<List<MenuItem>> {
        return menuDao.observeByCoffeeShopId(coffeeShopId).map { menuItemList ->
            menuItemList.toModel()
        }
    }

    override fun getByIdsStream(idList: List<Long>): Flow<List<MenuItem>> {
        return menuDao.observeByIds(idList).map { menuItemList ->
            menuItemList.toModel()
        }
    }

    override suspend fun getMenuItemById(id: Long): MenuItem? {
        return menuDao.getById(id)?.toModel()
    }
}
