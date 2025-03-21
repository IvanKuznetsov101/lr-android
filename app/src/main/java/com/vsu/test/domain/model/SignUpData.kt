package com.vsu.test.domain.model

import java.time.LocalDate

data class SignUpData(
    val fullName: String,
    val username: String,
    val password: String,
    val email: String,
    val dateOfBirth: LocalDate
)