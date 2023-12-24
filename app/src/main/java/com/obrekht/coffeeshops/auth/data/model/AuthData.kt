package com.obrekht.coffeeshops.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val login: String,
    val password: String
)
