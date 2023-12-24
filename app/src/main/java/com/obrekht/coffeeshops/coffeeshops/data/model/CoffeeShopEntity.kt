package com.obrekht.coffeeshops.coffeeshops.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("coffee_shop")
data class CoffeeShopEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    @Embedded
    val point: CoffeeShopPoint
)

fun CoffeeShopEntity.toApiModel() = CoffeeShopApiModel(id, name, point)
fun CoffeeShopApiModel.toEntity() = CoffeeShopEntity(id, name, point)

fun List<CoffeeShopEntity>.toApiModel() = map(CoffeeShopEntity::toApiModel)
fun List<CoffeeShopApiModel>.toEntity() = map(CoffeeShopApiModel::toEntity)
