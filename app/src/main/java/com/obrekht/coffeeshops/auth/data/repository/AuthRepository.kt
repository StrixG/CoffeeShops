package com.obrekht.coffeeshops.auth.data.repository

import com.obrekht.coffeeshops.auth.data.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val state: StateFlow<AuthState>

    suspend fun signUp(email: String, password: String)
    suspend fun logIn(email: String, password: String)

    suspend fun logOut()
}
