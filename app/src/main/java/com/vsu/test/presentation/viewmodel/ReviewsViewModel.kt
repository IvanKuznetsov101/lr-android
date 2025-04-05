package com.vsu.test.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.vsu.test.data.api.model.dto.ExtendedProfileDTO
import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.domain.model.ProfileWithImage
import com.vsu.test.domain.usecase.CreateProfileUseCase
import com.vsu.test.domain.usecase.CreateReviewUseCase
import com.vsu.test.domain.usecase.GetProfileByIdUseCase
import com.vsu.test.domain.usecase.GetProfileWithDetailsByIdUseCase
import com.vsu.test.domain.usecase.GetReviewByProfileIdsUseCase
import com.vsu.test.domain.usecase.GetReviewsByProfileIdUseCase
import com.vsu.test.domain.usecase.MoreState
import com.vsu.test.presentation.viewmodel.EditProfileViewModel.EditProfileState
import com.vsu.test.presentation.viewmodel.ProfileViewModel.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    private val getReviewsByProfileIdUseCase: GetReviewsByProfileIdUseCase,
    private val getReviewByProfileIdsUseCase: GetReviewByProfileIdsUseCase,
    private val getProfileWithDetailsByIdUseCase: GetProfileWithDetailsByIdUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val tokenManager: TokenManager

) : ViewModel() {

    private val _reviewsState = MutableStateFlow<ReviewsState>(ReviewsState.Loading)
    val reviewsState: StateFlow<ReviewsState> = _reviewsState

    fun loadReviews(profileId: Long) {
        viewModelScope.launch {
            _reviewsState.value = ReviewsState.Loading
            try {
                val profile = getProfileWithDetailsByIdUseCase(profileId)
                val reviews = getReviewsByProfileIdUseCase(profileId)
                if(profile != null){
                    _reviewsState.value = ReviewsState.Success(profile, reviews)
                }
            } catch (e: Exception) {
                _reviewsState.value = ReviewsState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }
    fun loadReviewForEdit(toProfileId: Long){
        viewModelScope.launch {
            _reviewsState.value = ReviewsState.Loading
            try {
                val fromProfileId = tokenManager.getId()
                val review = getReviewByProfileIdsUseCase(fromProfileId, toProfileId)
                _reviewsState.value = ReviewsState.EditReview(review)
            } catch (e: Exception) {
                _reviewsState.value = ReviewsState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }
    fun submitReview(onSuccess: () -> Unit) {
        val currentState = _reviewsState.value as? ReviewsState.EditReview ?: return
        viewModelScope.launch {
            try {
                val currentState = _reviewsState.value as? ReviewsState.EditReview ?: return@launch
                createReviewUseCase.invoke(
                    currentState.review.fromProfileId,
                    currentState.review.toProfileId,
                    currentState.review.text,
                    currentState.review.rating
                )
                onSuccess()
            } catch (e: Exception) {
                _reviewsState.value = ReviewsState.EditReview(
                    review = currentState.review,
                    validationErrorMessage = e.message ?: "Ошибка отправки"
                )
            }
        }
    }

    fun updateRating(newRating: Int) {
        val currentState = _reviewsState.value as? ReviewsState.EditReview ?: return
        val updatedReview = currentState.review.copy(
            rating = newRating
        )
        _reviewsState.value = currentState.copy(review = updatedReview)
    }

    fun updateText(newText: String) {
        val currentState = _reviewsState.value as? ReviewsState.EditReview ?: return
        val updatedReview = currentState.review.copy(
            text = newText
        )
        _reviewsState.value = currentState.copy(review = updatedReview)
    }

    sealed class ReviewsState {
        object Loading : ReviewsState()
        data class Success(
            val profile: ProfileWithDetails,
            val reviews: List<ReviewWithProfile>?) : ReviewsState()
        data class EditReview(
            val review: ReviewWithProfile,
            val validationErrorMessage: String? = null
        ) : ReviewsState()
        data class Error(val message: String) : ReviewsState()
    }
    sealed class ReviewValidationResult{
        data class Error(val message: String) : ReviewValidationResult()
        object Success: ReviewValidationResult()

    }
}