package com.obrekht.coffeeshops.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.obrekht.coffeeshops.BuildConfig
import com.obrekht.coffeeshops.auth.data.local.AuthManager
import com.obrekht.coffeeshops.auth.data.model.AuthState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(authManager: AuthManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val authState = authManager.state.value
                val request = if (authState is AuthState.Authorized) {
                    val newRequest = chain.request().newBuilder().apply {
                        addHeader("Cache-Control", "no-cache")
                        addHeader("Authorization", "Bearer ${authState.token}")
                    }

                    newRequest.build()
                } else {
                    chain.request()
                }

                return@addInterceptor chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
        }

        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .build()
    }
}
