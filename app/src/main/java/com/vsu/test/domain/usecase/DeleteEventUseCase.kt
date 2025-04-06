package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(

    private val eventRepository: EventRepository
) {
    suspend fun invoke(id: Long) = eventRepository.deleteEvent(id = id)
}