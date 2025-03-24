package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetActualEventByProfileId @Inject constructor(
    private val eventRepository: EventRepository,
    private val lightRoomRepository: LightRoomRepository
) {
    suspend fun invoke(profileId: Long): Pair<EventDTO?, LightRoomDTO?> {
        val eventResponse = eventRepository.getActualEventByProfileId(profileId)
        if(eventResponse is NetworkResult.Success){
            val lightRoomResponse = lightRoomRepository.getLightRoomByEventID(eventResponse.data!!.id)
            if (lightRoomResponse is NetworkResult.Success && lightRoomResponse.data != null) {
                return Pair(eventResponse.data, lightRoomResponse.data)
            }
        }
        return Pair(null, null)
    }
}