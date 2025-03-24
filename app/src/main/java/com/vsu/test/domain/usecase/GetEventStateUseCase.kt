package com.vsu.test.domain.usecase


import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.model.EventWithLightRoomData
import com.vsu.test.domain.model.LocationData
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetEventStateUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val profileRepository: ProfileRepository,
    private val lightRoomRepository: LightRoomRepository) {
    suspend operator fun invoke(locationData: LocationData): MoreState {
        val eventsInRadius = mutableListOf<EventWithLightRoomData>()
        val profileEventResponse = eventRepository.getActualEventByProfileId(locationData.id)
        if (profileEventResponse is NetworkResult.Success) {
            val profileLightRoomResponse =
                lightRoomRepository.getLightRoomByEventID(profileEventResponse.data!!.id)
            if (profileLightRoomResponse is NetworkResult.Success && profileLightRoomResponse.data != null) {
                return MoreState.UserEvent(
                    EventWithLightRoomData(
                        profileEventResponse.data,
                        profileLightRoomResponse.data
                    )
                )
            }
        } else {
            val inRadiusEventResponse =
                profileRepository.updateProfileCoordinatesAndGetEvents(locationData)
            if (inRadiusEventResponse is NetworkResult.Success && inRadiusEventResponse.data?.size!! > 0) {
                inRadiusEventResponse.data.forEach { event ->
                    val inRadiusLightRoomResponse =
                        lightRoomRepository.getLightRoomByEventID(event.id)
                    if (inRadiusLightRoomResponse is NetworkResult.Success && inRadiusLightRoomResponse.data != null) {
                        eventsInRadius.add(
                            EventWithLightRoomData(
                                event,
                                inRadiusLightRoomResponse.data
                            )
                        )
                    }
                }
                return MoreState.EventsInRadius(eventsInRadius)
            }
        }
        return MoreState.NoEvents
    }
}
sealed class MoreState {
    data class UserEvent(val eventWithLightRoom: EventWithLightRoomData) : MoreState()
    data class EventsInRadius(val eventsWithLightRoom: List<EventWithLightRoomData>) : MoreState()
    object NoEvents : MoreState()
}


//        val profileResponse = eventRepository.getActualEventByProfileId(locationData.id)
//        return if (profileResponse is NetworkResult.Success && profileResponse.data != null) {
//            val eventDTO = profileResponse.data
//            MoreState.UserEvent(eventDTO)
//        } else {
//            val eventResponse = profileRepository.updateProfileCoordinatesAndGetEvents(locationData)
//            if (eventResponse  is NetworkResult.Success && eventResponse.data?.size!! > 0){
//                val events = eventResponse.data.toList()
//                MoreState.EventsInRadius(events)
//            }
//            else MoreState.NoEvents
//        }
//    }