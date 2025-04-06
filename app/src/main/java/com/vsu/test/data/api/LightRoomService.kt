package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.api.model.request.LightRoomRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface LightRoomService {
    @GET("/api/lightroom/in-area")
    suspend fun getLightRoomsInArea(
        @Query("swLat") swLat: Double,
        @Query("swLon") swLon: Double,
        @Query("neLat") neLat: Double,
        @Query("neLon") neLon: Double
    ): Response<List<LightRoomDTO>>

    @POST("/api/lightroom")
    suspend fun createLightRoom(@Body lightRoomRequest: LightRoomRequest): Response<LightRoomDTO>

    @DELETE("/api/lightroom/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<LightRoomDTO>

    @GET("/api/lightroom/event/{id}")
    suspend fun getLightRoomByEventId(@Path("id") id: Long): Response<LightRoomDTO>
}