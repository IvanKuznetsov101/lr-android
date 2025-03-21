package com.vsu.test.data.api.model.request

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String
)
