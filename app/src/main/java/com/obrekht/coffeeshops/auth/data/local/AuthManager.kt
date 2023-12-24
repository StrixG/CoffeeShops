package com.obrekht.coffeeshops.auth.data.local

import com.obrekht.coffeeshops.auth.data.model.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor() {

    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Unauthorized)
    val state = _state.asStateFlow()

    fun authorize(token: String) {
        _state.update {
            AuthState.Authorized(token)
        }
    }

    fun logout() {
        _state.value = AuthState.Unauthorized
    }

}
