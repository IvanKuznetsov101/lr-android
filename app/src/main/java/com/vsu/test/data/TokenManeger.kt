package com.vsu.test.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow(false)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    init {
        _authState.value = isLoggedIn()
    }

    fun saveTokens(accessToken: String, refreshToken: String, id: Long) {
        prefs.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putLong("id", id)
            commit()
        }
        _authState.value = true
    }

    fun saveAccessToken(accessToken: String) {
        prefs.edit().apply {
            putString("access_token", accessToken)
            commit()
        }
        _authState.value = true
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)
    fun getId(): Long = prefs.getLong("id", -1L )

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun isLoggedIn(): Boolean {
        return prefs.contains("access_token")
                && prefs.contains("refresh_token")
    }

    fun clearTokens() {
        prefs.edit().apply {
            remove("access_token")
            remove("refresh_token")
            commit() // Синхронное удаление
        }
        _authState.value = false // Устанавливаем состояние в false
    }
}