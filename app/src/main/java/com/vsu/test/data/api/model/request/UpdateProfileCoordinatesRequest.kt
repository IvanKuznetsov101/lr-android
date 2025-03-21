package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileCoordinatesRequest(
    @SerializedName("id")
    val id: Long,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
