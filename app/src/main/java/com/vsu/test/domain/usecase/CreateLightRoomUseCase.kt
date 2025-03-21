package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.domain.model.EventData
import javax.inject.Inject

class CreateLightRoomUseCase @Inject constructor(
private val lightRoomRepository: LightRoomRepository
) {
    suspend fun invoke(latitude: Double, longitude: Double, idEvent: Long) =
        lightRoomRepository.createLightRoom(latitude = latitude, longitude = longitude, idEvent = idEvent)
}