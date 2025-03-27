package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class UpdateProfileRequest (
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
)