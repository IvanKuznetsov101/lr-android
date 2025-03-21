package com.vsu.test.data.interceptors

import android.util.Log
import com.vsu.test.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        // Не добавляем токен для auth-эндпоинтов
        if (url.contains("api/auth/login") || url.contains("api/auth/token") || url.contains("api/auth/refresh")) {
            return chain.proceed(originalRequest)
        }
        val accessToken = tokenManager.getAccessToken()
        val request = if (accessToken != null) {
            Log.d("AuthInterceptor", "Adding Authorization header to: $url")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            Log.d("AuthInterceptor", "No access token available for: $url")

            originalRequest
        }
        return chain.proceed(request)
    }
}