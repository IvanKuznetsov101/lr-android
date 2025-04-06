package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(

    private val eventRepository: EventRepository
) {
    suspend fun invoke(idProfile: Long, eventDTO: EventDTO) =
        eventRepository.createEvent(idProfile, eventDTO)

    suspend fun invoke(
        idProfile: Long,
        eventDTO: EventDTO,
        validate: Boolean
    ): NetworkResult<EventDTO> {
        return when {
            validate -> {
                requireNotNull(eventDTO.title) { "Title is required for event creation" }
                requireNotNull(eventDTO.description) { "Description is required for event creation" }
                requireNotNull(eventDTO.ageLimit) { "Age limit is required for event creation" }
                eventRepository.createEvent(profileId = idProfile, eventDTO = eventDTO)
            }

            else -> NetworkResult.Error(data = null, message = "Fail valid")
        }
    }
}