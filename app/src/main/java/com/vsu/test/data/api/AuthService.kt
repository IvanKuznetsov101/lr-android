package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.JwtResponse
import com.vsu.test.data.api.model.request.JwtRequest
import com.vsu.test.data.api.model.request.RefreshJwtRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login")
    suspend fun login(@Body request: JwtRequest): Response<JwtResponse>

    @POST("api/auth/token")
    fun getNewAccessToken(@Body request: RefreshJwtRequest): Call<JwtResponse>

    @POST("api/auth/refresh")
    suspend fun refreshTokens(@Body request: RefreshJwtRequest): Response<JwtResponse>
}