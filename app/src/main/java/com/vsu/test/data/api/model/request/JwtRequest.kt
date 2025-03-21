package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class JwtRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)