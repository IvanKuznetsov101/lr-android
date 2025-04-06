package com.vsu.test.data.repository

import com.vsu.test.data.api.ReviewService
import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.data.api.model.request.CreateReviewRequest
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) : BaseApiResponse() {
    suspend fun getReviewsByProfileId(id: Long): NetworkResult<List<ReviewWithProfile>> {
        return safeApiCall { reviewService.getReviewsByProfileId(id) }
    }

    suspend fun createReview(
        fromProfileId: Long, toProfileId: Long,
        text: String, rating: Int
    ): NetworkResult<Long> {
        val createReviewRequest = CreateReviewRequest(
            fromProfileId = fromProfileId, toProfileId = toProfileId,
            text = text, rating = rating
        )
        return safeApiCall { reviewService.createReview(createReviewRequest) }
    }

    suspend fun getAverageRatingByProfileId(id: Long) =
        safeApiCall { reviewService.getAverageRatingWithCountByProfileId(id) }

    suspend fun getReviewByProfileIds(fromProfileId: Long, toProfileId: Long) = safeApiCall {reviewService.getReviewByProfileIds(fromProfileId, toProfileId)  }

}