package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class EventRequest(
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("ageLimit")
    val ageLimit: Int?,
    @SerializedName("profileId")
    val profileId: Long?
)
