package com.obrekht.coffeeshops.auth.data.model

sealed interface AuthState {
    data object Unauthorized : AuthState
    data class Authorized(val token: String) : AuthState
}
