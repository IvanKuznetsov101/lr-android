package com.vsu.test.data.api.model.dto

import java.time.LocalDate

data class ExtendedProfileDTO(
    val id: Long,
    val fullName: String,
    val username: String,
    val email: String,
    val date_of_birth: LocalDate
)
