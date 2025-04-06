package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.data.repository.VisitorRepository
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.domain.model.LocationData
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetEventStateUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val profileRepository: ProfileRepository,
    private val lightRoomRepository: LightRoomRepository,
    private val visitorRepository: VisitorRepository,
    private val getEventWithDetailsByLightRoomIdUseCase: GetEventWithDetailsByLightRoomIdUseCase
) {
    suspend operator fun invoke(locationData: LocationData): MoreState {
        val profileEvent = eventRepository.getActualEventByProfileId(locationData.id)
        if (profileEvent is NetworkResult.Success) {
            val profileLightRoom = getLightRoomByEventId(profileEvent.data!!.id)
            if (profileLightRoom != null) {
                val profileEventWithDetails =
                    getEventWithDetailsByLightRoomIdUseCase.invoke(profileLightRoom)
                return MoreState.UserEvent(profileEventWithDetails!!)
            }
        }

        val inRadiusEvents = profileRepository.updateProfileCoordinatesAndGetEvents(locationData)
        if (inRadiusEvents !is NetworkResult.Success || inRadiusEvents.data.isNullOrEmpty()) {
            return MoreState.NoEvents
        }

        val eventsInRadius = inRadiusEvents.data.mapNotNull { event ->
            val lightRoom = getLightRoomByEventId(event.id) ?: return@mapNotNull null
            getEventWithDetailsByLightRoomIdUseCase.invoke(lightRoom)
        }
        return MoreState.EventsInRadius(eventsInRadius)
    }

    private suspend fun getLightRoomByEventId(eventId: Long): LightRoomDTO? {
        val response = lightRoomRepository.getLightRoomByEventID(eventId)
        return if (response is NetworkResult.Success) response.data else null
    }

    private suspend fun getVisitorCountByLightRoomId(lightRoomId: Long): Long {
        val response = visitorRepository.getVisitorCountByLightRoomId(lightRoomId)
        return if (response is NetworkResult.Success) response.data!! else 0L
    }
}

sealed class MoreState {
    object Loading : MoreState()
    data class UserEvent(val eventWithDetails: EventWithDetails) : MoreState()
    data class EventsInRadius(val eventsWithDetails: List<EventWithDetails>) : MoreState()
    object NoEvents : MoreState()
}