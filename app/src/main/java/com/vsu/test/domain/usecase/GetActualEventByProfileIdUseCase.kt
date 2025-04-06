package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.EventRepository
import javax.inject.Inject

class GetActualEventByProfileIdUseCase @Inject constructor(

    private val eventRepository: EventRepository
) {
    suspend fun invoke(profileId: Long) = eventRepository.getActualEventByProfileId(profileId)
}