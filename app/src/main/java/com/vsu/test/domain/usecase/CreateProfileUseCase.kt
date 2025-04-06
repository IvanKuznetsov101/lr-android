package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.SignUpData
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class CreateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun invoke(signUpData: SignUpData): NetworkResult<ProfileDTO> {
        if (signUpData.fullName.length < 5) {
            throw IllegalStateException("В имени должно быть больше 5 символов")
        }
        if (signUpData.username.length < 5) {
            throw IllegalStateException("В имени должно быть больше 5 символов")
        }
        if (signUpData.email.isEmpty()) {
            throw IllegalStateException("Почта не должна быть пустой")
        }
        return profileRepository.createProfile(signUpData)
    }

}