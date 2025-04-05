package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class LightRoomRequest(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("eventId")
    val eventId: Long
)
