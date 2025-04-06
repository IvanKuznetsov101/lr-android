package com.vsu.test.domain.model

import com.vsu.test.data.api.model.dto.ExtendedProfileDTO

data class ProfileWithImage(
    val profile: ExtendedProfileDTO,
    val profileImageUrl: String?
)