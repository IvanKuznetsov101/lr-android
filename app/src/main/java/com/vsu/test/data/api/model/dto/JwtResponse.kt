package com.vsu.test.data.api.model.dto

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
    val profileId: Long
)