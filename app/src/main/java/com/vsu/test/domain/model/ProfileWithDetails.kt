package com.vsu.test.domain.model

import com.vsu.test.data.api.model.dto.AverageRatingWithCount
import com.vsu.test.data.api.model.dto.ProfileDTO

data class ProfileWithDetails(
    val profile: ProfileDTO,
    val profileImageUrl: String?,
    val ratingWithCount: AverageRatingWithCount
)