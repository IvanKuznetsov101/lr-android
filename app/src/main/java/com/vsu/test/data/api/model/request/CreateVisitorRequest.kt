package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class CreateVisitorRequest(
    @SerializedName("profileId")
    val profileId: Long,
    @SerializedName("lightRoomId")
    val lightRoomId: Long,
)
