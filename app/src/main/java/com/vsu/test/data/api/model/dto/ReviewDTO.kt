package com.vsu.test.data.api.model.dto

import java.time.LocalDate

data class ReviewDTO(
    var id: Long,
    var fromUserId: Long,
    var toUserId: Long,
    var text: String,
    var rating: Int,
    var date: LocalDate
)
