package com.vsu.test.data.repository

import com.vsu.test.data.storage.TokenManager
import com.vsu.test.data.api.AuthService
import com.vsu.test.data.api.model.dto.JwtResponse
import com.vsu.test.data.api.model.request.JwtRequest
import com.vsu.test.data.api.model.request.RefreshJwtRequest
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : BaseApiResponse() {
    suspend fun login(username: String, password: String): NetworkResult<JwtResponse> {
        val jwtResponse = safeApiCall { authService.login(JwtRequest(username, password)) }
        if (jwtResponse is NetworkResult.Success) {
            jwtResponse.data?.let {
                tokenManager.saveTokens(it.accessToken, it.refreshToken, it.profileId)

            }
        }
        return jwtResponse
    }

    suspend fun refreshTokens(refreshToken: String): NetworkResult<JwtResponse> {
        val jwtResponse = safeApiCall { authService.refreshTokens(RefreshJwtRequest(refreshToken)) }
        if (jwtResponse is NetworkResult.Success) {
            jwtResponse.data?.let {
                tokenManager.saveTokens(
                    it.accessToken,
                    it.refreshToken,
                    it.profileId
                )
            }
        }
        return jwtResponse
    }
}