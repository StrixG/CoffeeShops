package com.obrekht.coffeeshops.coffeeshops.ui.model

import com.obrekht.coffeeshops.coffeeshops.data.model.CoffeeShopPoint

data class CoffeeShop(
    val id: Long,
    val name: String,
    val point: CoffeeShopPoint,
    val distance: Long
)
