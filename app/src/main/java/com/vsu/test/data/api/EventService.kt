package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.request.EventRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {
    @GET("/api/events/profile/{profileId}")
    suspend fun getEventByProfileId(@Path("profileId") profileId: Long, @Query("type") type: String = "not-used"): Response<List<EventDTO>>

    @GET("/api/events/profile/{profileId}")
    suspend fun getActualEventByProfileId(@Path("profileId") profileId: Long, @Query("type") type: String = "actual-event"): Response<EventDTO>

    @POST("/api/events")
    suspend fun createEvent(@Body eventRequest: EventRequest): Response<EventDTO>

    @PUT("/api/events/{id}")
    suspend fun updateEvent(@Path("id") id: Long, @Body eventRequest: EventRequest):Response<EventDTO>

    @DELETE("/api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<EventDTO>

    @GET("/api/events/lightRoom/{lightRoomId}")
    suspend fun getEventByLightRoomId(@Path("lightRoomId") lightRoomId: Long):Response<EventDTO>
}
