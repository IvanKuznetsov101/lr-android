package com.vsu.test.data.api.model.dto

data class ReviewWithProfile(
    var id: Long,
    var fromProfileId: Long,
    var toProfileId: Long,
    var rating: Int,
    var text: String,
    var createdAt: String?,
    var fullName: String,
    var image: String
)
