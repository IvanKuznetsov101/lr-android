package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class DeleteLightRoomByIdUseCase @Inject constructor(
    private val lightRoomRepository: LightRoomRepository
) {
    suspend fun invoke(lightRoomId: Long): NetworkResult<LightRoomDTO> {
        return lightRoomRepository.deleteLightRoom(lightRoomId)
    }
}