package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.SignUpData
import javax.inject.Inject

class CreateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun invoke(signUpData: SignUpData) = profileRepository.createProfile(signUpData)

}