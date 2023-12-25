package com.obrekht.coffeeshops.cart.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("cart_item")
data class CartItemEntity(
    @PrimaryKey
    val menuItemId: Long,
    val count: Int
)
