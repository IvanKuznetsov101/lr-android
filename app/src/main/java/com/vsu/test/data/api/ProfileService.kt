package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.api.model.request.SignUpRequest
import com.vsu.test.data.api.model.request.UpdateProfileCoordinatesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProfileService {
    @POST("/api/profiles")
    suspend fun createProfile(@Body body: SignUpRequest): Response<ProfileDTO>

    @PUT("/api/profiles/coordinates")
    suspend fun updateProfileCoordinates(@Body body: UpdateProfileCoordinatesRequest): Response<List<Long>>
}