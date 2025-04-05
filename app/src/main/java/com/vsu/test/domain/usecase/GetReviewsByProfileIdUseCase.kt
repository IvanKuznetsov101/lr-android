package com.vsu.test.domain.usecase


import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.data.repository.ReviewRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetReviewsByProfileIdUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(profileId: Long): List<ReviewWithProfile>? {
        val reviews = reviewRepository.getReviewsByProfileId(profileId)
        if(reviews is NetworkResult.Success){
            return reviews.data
        }
        return emptyList()
    }
}