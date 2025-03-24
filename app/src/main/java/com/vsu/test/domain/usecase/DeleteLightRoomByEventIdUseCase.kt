package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class DeleteLightRoomByEventIdUseCase @Inject constructor(
private val lightRoomRepository: LightRoomRepository
) {
    suspend fun invoke(eventId: Long): NetworkResult<LightRoomDTO> {
        val response = lightRoomRepository.getLightRoomByEventID(eventId)
        if (response is NetworkResult.Success){
            return lightRoomRepository.deleteLightRoom(id = response.data!!.toLong())
        }
        return  NetworkResult.Error(data = null, message = "Fail delete")
    }
}