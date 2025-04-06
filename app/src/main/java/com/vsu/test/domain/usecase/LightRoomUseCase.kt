package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.LightRoomRepository
import com.yandex.mapkit.geometry.BoundingBox
import javax.inject.Inject

class LightRoomUseCase @Inject constructor(
    private val lightRoomRepository: LightRoomRepository
) {
    suspend fun invoke(box: BoundingBox) = lightRoomRepository.getLightRoomsInArea(box)
}