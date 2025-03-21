package com.vsu.test.domain.usecase

import com.vsu.test.data.TokenManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke() {
        tokenManager.clearTokens()
    }
}