package com.obrekht.coffeeshops.coffeeshops.ui

import androidx.navigation.NavController
import com.obrekht.coffeeshops.NavMainDirections
import com.obrekht.coffeeshops.R

fun NavController.navigateToNearbyCoffeeShops(asStartDestination: Boolean = false) {
    if (asStartDestination) {
        graph.setStartDestination(R.id.nearby_coffee_shops_fragment)
    }

    val action = NavMainDirections.actionGlobalNearbyCoffeeShopsFragment()
    navigate(action)
}
