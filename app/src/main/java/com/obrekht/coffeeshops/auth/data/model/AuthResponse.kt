package com.obrekht.coffeeshops.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val tokenLifetime: Int
)
