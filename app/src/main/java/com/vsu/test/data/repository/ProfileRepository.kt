package com.vsu.test.data.repository

import com.vsu.test.data.api.ProfileService
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.ExtendedProfileDTO
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.api.model.request.SignUpRequest
import com.vsu.test.data.api.model.request.UpdateProfileCoordinatesRequest
import com.vsu.test.data.api.model.request.UpdateProfileRequest
import com.vsu.test.domain.model.LocationData
import com.vsu.test.domain.model.SignUpData
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileService: ProfileService
) : BaseApiResponse() {
    suspend fun createProfile(signUpData: SignUpData): NetworkResult<ProfileDTO> {
        val request = SignUpRequest(
            fullName = signUpData.fullName,
            username = signUpData.username,
            password = signUpData.password,
            email = signUpData.email,
            dateOfBirth = signUpData.dateOfBirth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        return safeApiCall { profileService.createProfile(request) }
    }

    suspend fun updateProfileCoordinatesAndGetIds(locationData: LocationData): NetworkResult<List<Long>> {
        val request = UpdateProfileCoordinatesRequest(
            id = locationData.id,
            latitude = locationData.latitude,
            longitude = locationData.longitude
        )
        return safeApiCall { profileService.updateProfileCoordinatesAndGetIds(request) }
    }

    suspend fun updateProfileCoordinatesAndGetEvents(locationData: LocationData): NetworkResult<List<EventDTO>> {
        val request = UpdateProfileCoordinatesRequest(
            id = locationData.id,
            latitude = locationData.latitude,
            longitude = locationData.longitude
        )
        return safeApiCall { profileService.updateProfileCoordinatesAndGetEvents(request) }
    }

    suspend fun getProfileById(id: Long) = safeApiCall { profileService.getProfileById(id) }

    suspend fun getFullProfileById(id: Long) = safeApiCall { profileService.getFullProfileById(id) }

    suspend fun getProfileByEventId(id: Long) =
        safeApiCall { profileService.getProfileByEventId(id) }

    suspend fun updateProfile(extendedProfileDTO: ExtendedProfileDTO): NetworkResult<ProfileDTO> {
        val profileId = extendedProfileDTO.id
        val updateProfileRequest = UpdateProfileRequest(
            fullName = extendedProfileDTO.fullName,
            username = extendedProfileDTO.username,
            email = extendedProfileDTO.email,
            dateOfBirth = extendedProfileDTO.date_of_birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )
        return safeApiCall { profileService.updateProfile(profileId, updateProfileRequest) }
    }
}
