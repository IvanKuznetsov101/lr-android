package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.domain.model.EventData
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetActualEventByProfileIdUseCase @Inject constructor(

private val eventRepository: EventRepository
) {
    suspend fun invoke(profileId: Long) = eventRepository.getActualEventByProfileId(profileId)
}