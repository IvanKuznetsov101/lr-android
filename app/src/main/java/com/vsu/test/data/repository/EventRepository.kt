package com.vsu.test.data.repository

import com.vsu.test.data.api.EventService
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LastEvent
import com.vsu.test.data.api.model.request.EventRequest
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventService: EventService
) : BaseApiResponse() {
    suspend fun getEventByProfileId(id: Long): NetworkResult<List<EventDTO>> {
        return safeApiCall { eventService.getEventByProfileId(profileId = id) }
    }

    suspend fun getActualEventByProfileId(id: Long): NetworkResult<EventDTO> {
        return safeApiCall { eventService.getActualEventByProfileId(profileId = id) }
    }

    suspend fun getLastEventsByProfileId(id: Long): NetworkResult<List<LastEvent>> {
        return safeApiCall { eventService.getLastEventsByProfileId(profileId = id) }
    }

    suspend fun createEvent(profileId: Long, eventDTO: EventDTO): NetworkResult<EventDTO> {
        val eventRequest = EventRequest(
            title = eventDTO.title, description = eventDTO.description,
            ageLimit = eventDTO.ageLimit, profileId = profileId
        )
        return safeApiCall { eventService.createEvent(eventRequest = eventRequest) }
    }

    suspend fun updateEvent(profileId: Long, eventDTO: EventDTO): NetworkResult<EventDTO> {
        val eventRequest = EventRequest(
            title = eventDTO.title, description = eventDTO.description,
            ageLimit = eventDTO.ageLimit, profileId = profileId
        )
        return safeApiCall {
            eventService.updateEvent(
                id = eventDTO.id!!,
                eventRequest = eventRequest
            )
        } //todo
    }

    suspend fun deleteEvent(id: Long): NetworkResult<EventDTO> {
        return safeApiCall { eventService.deleteEvent(id = id) }
    }

    suspend fun getEventByLightRoomId(lightRoomId: Long): NetworkResult<EventDTO> {
        return safeApiCall { eventService.getEventByLightRoomId(lightRoomId = lightRoomId) }
    }
}