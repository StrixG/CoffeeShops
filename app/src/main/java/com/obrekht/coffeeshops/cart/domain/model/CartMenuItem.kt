package com.obrekht.coffeeshops.cart.domain.model

data class CartMenuItem(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val count: Int = 0
)
