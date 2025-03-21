package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.EventRepository
import javax.inject.Inject

class EventUseCase @Inject constructor(

private val eventRepository: EventRepository
) {
    suspend fun invoke(id: Long) = eventRepository.getEventByProfileId(id)
}