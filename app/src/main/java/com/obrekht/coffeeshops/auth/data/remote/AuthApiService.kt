package com.obrekht.coffeeshops.auth.data.remote

import com.obrekht.coffeeshops.auth.data.model.AuthData
import com.obrekht.coffeeshops.auth.data.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun signUp(@Body authData: AuthData): Response<AuthResponse>

    @POST("auth/login")
    suspend fun logIn(@Body authData: AuthData): Response<AuthResponse>
}
