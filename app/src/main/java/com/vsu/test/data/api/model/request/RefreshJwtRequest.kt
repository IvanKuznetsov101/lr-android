package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class RefreshJwtRequest(
    @SerializedName("refreshToken") val refreshToken: String
)