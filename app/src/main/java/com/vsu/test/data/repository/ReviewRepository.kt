package com.vsu.test.data.repository

import com.vsu.test.data.api.ReviewService
import com.vsu.test.data.api.model.dto.ReviewDTO
import com.vsu.test.data.api.model.request.CreateReviewRequest
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) : BaseApiResponse() {
    suspend fun getReviewByProfileId(id: Long): NetworkResult<List<ReviewDTO>> {
        return safeApiCall { reviewService.getReviewsByProfileId(id) }
    }

    suspend fun createReview(
        fromUserId: Long, toUserId: Long,
        text: String, rating: Int
    ): NetworkResult<Long> {
        val createReviewRequest = CreateReviewRequest(
            fromUserId = fromUserId, toUserId = toUserId,
            text = text, rating = rating
        )
        return safeApiCall { reviewService.createReview(createReviewRequest) }
    }

    suspend fun getAverageRatingByProfileId(id: Long) =
        safeApiCall { reviewService.getAverageRatingWithCountByProfileId(id) }

}