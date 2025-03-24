package com.vsu.test.data.repository

import com.vsu.test.data.api.LightRoomService
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.api.model.request.LightRoomRequest
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import com.yandex.mapkit.geometry.BoundingBox
import javax.inject.Inject

class LightRoomRepository @Inject constructor(
    private val lightRoomService: LightRoomService
): BaseApiResponse() {
    suspend fun getLightRoomsInArea(box: BoundingBox): NetworkResult<List<LightRoomDTO>> {
        return safeApiCall { lightRoomService.getLightRoomsInArea(swLat = box.southWest.latitude,
            swLon = box.southWest.longitude,
            neLat = box.northEast.latitude,
            neLon = box.northEast.longitude) }
    }
    suspend fun createLightRoom(latitude: Double, longitude: Double, idEvent: Long): NetworkResult<LightRoomDTO> {
        val lightRoomRequest = LightRoomRequest(latitude = latitude, longitude = longitude, eventId = idEvent)
        return safeApiCall { lightRoomService.createLightRoom(lightRoomRequest) }
    }
    suspend fun deleteLightRoom(id: Long): NetworkResult<LightRoomDTO>{
        return safeApiCall { lightRoomService.deleteEvent(id = id) }
    }
    suspend fun getLightRoomByEventID(id:Long): NetworkResult<Long>{
        return safeApiCall { lightRoomService.getLightRoomByEventId(id) }
    }
}