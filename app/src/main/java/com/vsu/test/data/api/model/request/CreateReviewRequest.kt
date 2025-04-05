package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    @SerializedName("from_user_id")
    val fromProfileId: Long,
    @SerializedName("to_user_id")
    val toProfileId: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("rating")
    val rating: Int
)
