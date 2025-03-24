package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.LocationData
import javax.inject.Inject

class UpdateProfileCoordinatesAndGetIdsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend fun invoke(locationData: LocationData) = profileRepository.updateProfileCoordinatesAndGetIds(locationData)

}