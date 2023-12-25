package com.obrekht.coffeeshops.menu.ui

import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem

interface MenuItemInteractionListener {

    fun onClick(cartMenuItem: CartMenuItem, position: Int) {}
    fun onAdd(cartMenuItem: CartMenuItem, position: Int) {}
    fun onRemove(cartMenuItem: CartMenuItem, position: Int) {}
}
