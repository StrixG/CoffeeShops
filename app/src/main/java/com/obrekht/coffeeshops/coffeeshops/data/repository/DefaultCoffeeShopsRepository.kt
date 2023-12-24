package com.obrekht.coffeeshops.coffeeshops.data.repository

import com.obrekht.coffeeshops.coffeeshops.data.local.CoffeeShopsDao
import com.obrekht.coffeeshops.coffeeshops.data.model.toEntity
import com.obrekht.coffeeshops.coffeeshops.data.remote.CoffeeShopsApiService
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import com.obrekht.coffeeshops.core.data.model.EmptyBodyException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class DefaultCoffeeShopsRepository @Inject constructor(
    private val coffeeShopsDao: CoffeeShopsDao,
    private val coffeeShopsApiService: CoffeeShopsApiService
) : CoffeeShopsRepository {

    override suspend fun refreshAll() {
        val response = coffeeShopsApiService.getAll()
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val body = response.body() ?: throw EmptyBodyException()
        coffeeShopsDao.replaceAll(body.toEntity())
    }

    override fun getCoffeeShopsStream(): Flow<List<CoffeeShop>> {
        return coffeeShopsDao.observeAll().map { coffeeShopList ->
            coffeeShopList.map {
                // TODO: Calculate distance
                CoffeeShop(it.id, it.name, it.point, it.point.latitude.toLong())
            }
        }
    }

    override suspend fun getCoffeeShopById(id: Long): CoffeeShop? {
        return coffeeShopsDao.getById(id)?.run {
            CoffeeShop(id, name, point, point.latitude.toLong())
        }
    }
}
