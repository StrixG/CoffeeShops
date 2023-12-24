package com.obrekht.coffeeshops.coffeeshops.data.repository

import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import kotlinx.coroutines.flow.Flow

interface CoffeeShopsRepository {

    suspend fun refreshAll()

    fun getCoffeeShopsStream(): Flow<List<CoffeeShop>>
}
