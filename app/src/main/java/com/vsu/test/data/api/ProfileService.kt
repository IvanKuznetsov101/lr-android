package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.api.model.request.SignUpRequest
import com.vsu.test.data.api.model.request.UpdateProfileCoordinatesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ProfileService {
    @POST("/api/profiles")
    suspend fun createProfile(@Body body: SignUpRequest): Response<ProfileDTO>

    @PUT("/api/profiles/coordinates")
    suspend fun updateProfileCoordinatesAndGetIds(@Body body: UpdateProfileCoordinatesRequest, @Query("type") type: String = "get-ids"): Response<List<Long>>

    @PUT("/api/profiles/coordinates")
    suspend fun updateProfileCoordinatesAndGetEvents(@Body body: UpdateProfileCoordinatesRequest, @Query("type") type: String = "get-events"): Response<List<EventDTO>>
}