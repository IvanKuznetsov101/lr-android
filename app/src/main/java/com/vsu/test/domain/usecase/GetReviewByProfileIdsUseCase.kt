package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.data.repository.ReviewRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetReviewByProfileIdsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(fromProfileId: Long, toProfileId: Long): ReviewWithProfile {
        val response = reviewRepository.getReviewByProfileIds(fromProfileId, toProfileId)
        if (response is NetworkResult.Error) {
            throw IllegalArgumentException(response.message)
        }
        if (response.data == null) {
            throw IllegalArgumentException("data is null")
        }
        return response.data
    }
}