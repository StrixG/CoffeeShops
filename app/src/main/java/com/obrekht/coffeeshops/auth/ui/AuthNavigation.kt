package com.obrekht.coffeeshops.auth.ui

import androidx.navigation.NavController
import com.obrekht.coffeeshops.NavMainDirections
import com.obrekht.coffeeshops.R

fun NavController.navigateToSignUp(shouldLogOut: Boolean = false, asStartDestination: Boolean = false) {
    if (asStartDestination) {
        graph.setStartDestination(R.id.sign_up_fragment)
    }

    val action = NavMainDirections.actionGlobalSignUpFragment(shouldLogOut)
    navigate(action)
}
