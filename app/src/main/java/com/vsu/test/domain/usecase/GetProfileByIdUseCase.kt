package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.ImageRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.ProfileWithImage
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetProfileByIdUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository) {
    suspend operator fun invoke(profileId: Long): ProfileWithImage {
        val profileResponse = profileRepository.getFullProfileById(profileId)
        val imageResponse = imageRepository.getImageUrlByProfileId(profileId)

        val profileData = profileResponse.data ?: throw IllegalStateException("Profile data is null")
        val imageData = imageResponse.data

        return ProfileWithImage(profileData, imageData)
    }

}