package com.obrekht.coffeeshops.cart.domain

import com.obrekht.coffeeshops.cart.data.repository.CartRepository
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import com.obrekht.coffeeshops.menu.data.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartMenuItemsStreamUseCase @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository
) {

    operator fun invoke(coffeeShopId: Long = 0L): Flow<List<CartMenuItem>> {
        return if (coffeeShopId > 0) {
            menuRepository.getByCoffeeShopIdStream(coffeeShopId).flatMapLatest { menuItems ->
                val idList = menuItems.map { it.id }
                cartRepository.getByIdsStream(idList).map { cartItems ->
                    val idToCount = cartItems.associate { it.menuItemId to it.count }
                    menuItems.map {
                        val itemCount = idToCount[it.id] ?: 0
                        CartMenuItem(it.id, it.name, it.imageUrl, it.price, itemCount)
                    }
                }
            }
        } else {
            cartRepository.getAllStream().flatMapLatest { cartItems ->
                val idList = cartItems.map { it.menuItemId }
                val idToCount = cartItems.associate { it.menuItemId to it.count }
                menuRepository.getByIdsStream(idList).map { menuItems ->
                    menuItems.map {
                        val count = idToCount[it.id] ?: 0
                        CartMenuItem(it.id, it.name, it.imageUrl, it.price, count)
                    }
                }
            }
        }
    }
}
