package com.obrekht.coffeeshops.menu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("menu_item")
data class MenuItemEntity(
    @PrimaryKey
    val id: Long,
    val coffeeShopId: Long,
    val name: String,
    val imageUrl: String,
    val price: Int
)

fun MenuItemEntity.toModel() = MenuItem(id, name, imageUrl, price)
fun MenuItem.toEntity(coffeeShopId: Long) = MenuItemEntity(id, coffeeShopId, name, imageUrl, price)

fun List<MenuItemEntity>.toModel() = map(MenuItemEntity::toModel)
fun List<MenuItem>.toEntity(coffeeShopId: Long) = map { it.toEntity(coffeeShopId) }
