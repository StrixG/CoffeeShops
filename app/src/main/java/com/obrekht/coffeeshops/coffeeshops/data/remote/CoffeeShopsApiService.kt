package com.obrekht.coffeeshops.coffeeshops.data.remote

import com.obrekht.coffeeshops.coffeeshops.data.model.CoffeeShopApiModel
import retrofit2.Response
import retrofit2.http.GET

interface CoffeeShopsApiService {

    @GET("locations")
    suspend fun getAll(): Response<List<CoffeeShopApiModel>>
}
