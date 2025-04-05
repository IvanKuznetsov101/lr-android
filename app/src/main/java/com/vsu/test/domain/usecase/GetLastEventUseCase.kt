package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.LastEvent
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetLastEventUseCase @Inject constructor(

private val eventRepository: EventRepository
) {
    suspend fun invoke(id: Long) : List<LastEvent> {
        val response = eventRepository.getLastEventsByProfileId(id)
        if (response is NetworkResult.Success && response.data != null){
            return response.data
        }
        return emptyList()
    }
}