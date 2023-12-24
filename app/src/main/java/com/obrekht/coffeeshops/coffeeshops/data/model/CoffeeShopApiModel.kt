package com.obrekht.coffeeshops.coffeeshops.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CoffeeShopApiModel(
    val id: Long,
    val name: String,
    val point: CoffeeShopPoint
)
