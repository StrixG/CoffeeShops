package com.obrekht.coffeeshops.menu.data.remote

import com.obrekht.coffeeshops.menu.data.model.MenuItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MenuApiService {

    @GET("location/{id}/menu")
    suspend fun getById(@Path("id") coffeeShopId: Long): Response<List<MenuItem>>
}
