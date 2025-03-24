package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.LocationData
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetEventStateUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(locationData: LocationData): MoreState {
        val profileResponse = eventRepository.getActualEventByProfileId(locationData.id)
        return if (profileResponse is NetworkResult.Success && profileResponse.data != null) {
            val eventDTO = profileResponse.data
            MoreState.UserEvent(eventDTO)
        } else {
            val eventResponse = profileRepository.updateProfileCoordinatesAndGetEvents(locationData)
            if (eventResponse  is NetworkResult.Success && eventResponse.data?.size!! > 0){
                val events = eventResponse.data.toList()
                MoreState.EventsInRadius(events)
            }
            else MoreState.NoEvents
        }
    }
}

sealed class MoreState {
    data class UserEvent(val event: EventDTO) : MoreState()
    data class EventsInRadius(val events: List<EventDTO>) : MoreState()
    object NoEvents : MoreState()
}