package com.obrekht.coffeeshops.menu.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem (
    val id: Long,
    val name: String,
    @SerialName("imageURL")
    val imageUrl: String,
    val price: Int
)
