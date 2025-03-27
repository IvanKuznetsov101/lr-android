package com.vsu.test.domain.usecase


import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.ImageRepository
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.data.repository.ReviewRepository
import com.vsu.test.data.repository.VisitorRepository
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.data.storage.VisitorStorage
import com.vsu.test.data.storage.VisitorInfo
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetProfileWithDetailsByIdUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(profileId: Long): ProfileWithDetails? {
        val profileResponse = profileRepository.getProfileById(profileId)
        val profileImageUrlResponse = profileResponse.data?.let { imageRepository.getImageUrlByProfileId(it.id) }
        val averageRatingWithCountResponse =  profileResponse.data?.let {reviewRepository.getAverageRatingByProfileId(it.id) }
        return if (profileResponse is NetworkResult.Success && profileResponse.data != null &&
            averageRatingWithCountResponse is NetworkResult.Success && averageRatingWithCountResponse.data != null)
            ProfileWithDetails(
                profileResponse.data,
                profileImageUrlResponse?.data.toString(),
                averageRatingWithCountResponse.data
            ) else return null
    }
}