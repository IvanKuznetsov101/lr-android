package com.vsu.test.data

import com.vsu.test.data.api.AuthService
import com.vsu.test.data.api.model.request.RefreshJwtRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authService: AuthService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        return try {
            val call = authService.getNewAccessToken(RefreshJwtRequest(refreshToken))
            val refreshResponse = call.execute()
            if (refreshResponse.isSuccessful) {
                val newTokens = refreshResponse.body()!!
                tokenManager.saveAccessToken(newTokens.accessToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                tokenManager.clearTokens()
                null
            }
        } catch (e: Exception) {
            tokenManager.clearTokens()
            null
        }
    }
}