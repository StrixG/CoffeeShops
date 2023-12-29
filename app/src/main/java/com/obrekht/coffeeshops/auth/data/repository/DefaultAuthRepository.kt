package com.obrekht.coffeeshops.auth.data.repository

import com.obrekht.coffeeshops.auth.data.local.AuthManager
import com.obrekht.coffeeshops.auth.data.model.AuthData
import com.obrekht.coffeeshops.auth.data.model.AuthState
import com.obrekht.coffeeshops.auth.data.model.exception.AccountIsTakenException
import com.obrekht.coffeeshops.auth.data.model.exception.InvalidCredentialsException
import com.obrekht.coffeeshops.auth.data.remote.AuthApiService
import com.obrekht.coffeeshops.core.data.model.EmptyBodyException
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val authManager: AuthManager,
    private val authApiService: AuthApiService
) : AuthRepository {

    override val state: StateFlow<AuthState>
        get() = authManager.state

    override suspend fun signUp(email: String, password: String) {
        val response = authApiService.signUp(AuthData(email, password))
        if (!response.isSuccessful) {
            if (response.code() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                throw AccountIsTakenException()
            } else {
                throw HttpException(response)
            }
        }
        val body = response.body() ?: throw EmptyBodyException()
        authManager.authorize(body.token)
    }

    override suspend fun logIn(email: String, password: String) {
        val response = authApiService.logIn(AuthData(email, password))
        if (!response.isSuccessful) {
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw InvalidCredentialsException()
            } else {
                throw HttpException(response)
            }
        }
        val body = response.body() ?: throw EmptyBodyException()
        authManager.authorize(body.token)
    }

    override suspend fun logOut() = authManager.logOut()
}
