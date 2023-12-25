package com.obrekht.coffeeshops.cart.ui

import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem

interface CartItemInteractionListener {

    fun onAdd(cartMenuItem: CartMenuItem, position: Int) {}
    fun onRemove(cartMenuItem: CartMenuItem, position: Int) {}
}
