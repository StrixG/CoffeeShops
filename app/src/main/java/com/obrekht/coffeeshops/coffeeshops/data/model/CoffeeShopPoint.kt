package com.obrekht.coffeeshops.coffeeshops.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CoffeeShopPoint(
    val latitude: Double,
    val longitude: Double
)
