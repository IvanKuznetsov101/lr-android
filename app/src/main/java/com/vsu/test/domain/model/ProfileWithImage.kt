package com.vsu.test.domain.model

import com.vsu.test.data.api.model.dto.AverageRatingWithCount
import com.vsu.test.data.api.model.dto.ExtendedProfileDTO
import com.vsu.test.data.api.model.dto.ProfileDTO

data class ProfileWithImage(
    val profile: ExtendedProfileDTO,
    val profileImageUrl: String?
)