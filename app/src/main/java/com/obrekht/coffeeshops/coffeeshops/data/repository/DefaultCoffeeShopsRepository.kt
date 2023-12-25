package com.obrekht.coffeeshops.coffeeshops.data.repository

import com.obrekht.coffeeshops.coffeeshops.data.local.CoffeeShopsDao
import com.obrekht.coffeeshops.coffeeshops.data.model.toEntity
import com.obrekht.coffeeshops.coffeeshops.data.remote.CoffeeShopsApiService
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import com.obrekht.coffeeshops.core.data.model.EmptyBodyException
import com.obrekht.coffeeshops.geolocation.data.repository.GeoLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import retrofit2.HttpException
import javax.inject.Inject

class DefaultCoffeeShopsRepository @Inject constructor(
    private val coffeeShopsDao: CoffeeShopsDao,
    private val coffeeShopsApiService: CoffeeShopsApiService,
    private val geoLocationRepository: GeoLocationRepository
) : CoffeeShopsRepository {

    override suspend fun refreshAll() {
        geoLocationRepository.refreshCurrentLocation()

        val response = coffeeShopsApiService.getAll()
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val body = response.body() ?: throw EmptyBodyException()
        coffeeShopsDao.replaceAll(body.toEntity())
    }

    override fun getCoffeeShopsStream(): Flow<List<CoffeeShop>> {
        return coffeeShopsDao.observeAll()
            .combine(geoLocationRepository.currentLocation) { coffeeShopList, _ ->
                coffeeShopList.map {
                    val distance = geoLocationRepository.getDistanceToLocation(
                        it.point.latitude, it.point.longitude
                    )
                    CoffeeShop(it.id, it.name, it.point, distance?.toLong())
                }
            }
    }

    override suspend fun getCoffeeShopById(id: Long): CoffeeShop? {
        return coffeeShopsDao.getById(id)?.run {
            val distance = geoLocationRepository.getDistanceToLocation(
                point.latitude, point.longitude
            )
            CoffeeShop(id, name, point, distance?.toLong())
        }
    }
}
