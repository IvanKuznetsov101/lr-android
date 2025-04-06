package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.EventRepository
import javax.inject.Inject

class GetEventByLightRoomIdUseCase @Inject constructor(

    private val eventRepository: EventRepository
) {
    suspend fun invoke(lightRoomId: Long) =
        eventRepository.getEventByLightRoomId(lightRoomId = lightRoomId)
}