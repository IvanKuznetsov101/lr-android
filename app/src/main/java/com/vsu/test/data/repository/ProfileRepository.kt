package com.vsu.test.data.repository

import com.vsu.test.data.api.ProfileService
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.api.model.request.SignUpRequest
import com.vsu.test.data.api.model.request.UpdateProfileCoordinatesRequest
import com.vsu.test.domain.model.LocationData
import com.vsu.test.domain.model.SignUpData
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileService: ProfileService
): BaseApiResponse() {
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
    suspend fun updateProfileCoordinates(locationData: LocationData): NetworkResult<List<Long>> {
        val request = UpdateProfileCoordinatesRequest(
            id = locationData.id,
            latitude = locationData.latitude,
            longitude = locationData.longitude)
        return safeApiCall { profileService.updateProfileCoordinates(request) }
    }
}