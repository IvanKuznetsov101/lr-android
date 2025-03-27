package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.ExtendedProfileDTO
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.api.model.request.EventRequest
import com.vsu.test.data.api.model.request.SignUpRequest
import com.vsu.test.data.api.model.request.UpdateProfileCoordinatesRequest
import com.vsu.test.data.api.model.request.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import javax.net.ssl.ExtendedSSLSession

interface ProfileService {
    @POST("/api/profiles")
    suspend fun createProfile(@Body body: SignUpRequest): Response<ProfileDTO>

    @PUT("/api/profiles/coordinates")
    suspend fun updateProfileCoordinatesAndGetIds(@Body body: UpdateProfileCoordinatesRequest, @Query("type") type: String = "get-ids"): Response<List<Long>>

    @PUT("/api/profiles/coordinates")
    suspend fun updateProfileCoordinatesAndGetEvents(@Body body: UpdateProfileCoordinatesRequest, @Query("type") type: String = "get-events"): Response<List<EventDTO>>

    @PUT("/api/profiles/{id}")
    suspend fun updateProfile(@Path("id") id: Long, @Body updateProfileRequest: UpdateProfileRequest):Response<ProfileDTO>

    @GET("/api/profiles/{id}")
    suspend fun getProfileById(@Path("id") profileId: Long, @Query("type") type: String = "short"): Response<ProfileDTO>

    @GET("/api/profiles/{id}")
    suspend fun getFullProfileById(@Path("id") profileId: Long, @Query("type") type: String = "full"): Response<ExtendedProfileDTO>

    @GET("/api/profiles/event/{id}")
    suspend fun getProfileByEventId(@Path("id") eventId: Long): Response<ProfileDTO>
}