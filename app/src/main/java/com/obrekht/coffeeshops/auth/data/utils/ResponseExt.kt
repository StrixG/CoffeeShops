package com.obrekht.coffeeshops.auth.data.utils

import retrofit2.Response
import java.net.HttpURLConnection

val <T> Response<T>.isAuthTokenValid: Boolean
    get() = this.code() != HttpURLConnection.HTTP_UNAUTHORIZED &&
            this.errorBody()?.string() != "token is not valid or has expired"
