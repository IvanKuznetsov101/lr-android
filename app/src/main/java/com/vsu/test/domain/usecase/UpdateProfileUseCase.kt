package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.ExtendedProfileDTO
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.LocationData
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun invoke(extendedProfileDTO: ExtendedProfileDTO): NetworkResult<ProfileDTO>
    {
        if (extendedProfileDTO.fullName.length < 5){
            throw IllegalStateException("В имени должно быть больше 5 символов")
        }
        if (extendedProfileDTO.username.length < 5){
            throw IllegalStateException("В имени должно быть больше 5 символов")
        }
        if (extendedProfileDTO.email.isEmpty()){
            throw IllegalStateException("Почта не должна быть пустой")
        }
        val response = profileRepository.updateProfile(extendedProfileDTO)
        if (response is NetworkResult.Error){
            throw  IllegalStateException("Error updating profile: ${response.message}" )
        }
        return response
    }
}