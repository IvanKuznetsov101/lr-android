package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.ReviewRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend fun invoke(fromProfileId: Long, toProfileId: Long, text: String, rating: Int) {
        if (text.isBlank()) {
            throw IllegalArgumentException("Текст отзыва не может быть пустым")
        }
        if (rating < 1 || rating > 5) {
            throw IllegalArgumentException("Рейтинг должен быть от 1 до 5")
        }
        val response = reviewRepository.createReview(
            fromProfileId = fromProfileId,
            toProfileId = toProfileId,
            text = text,
            rating = rating
        )
        if (response is NetworkResult.Error) {
            throw IllegalArgumentException(response.message.toString())
        }
    }
}