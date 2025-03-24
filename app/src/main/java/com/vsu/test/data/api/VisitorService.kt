package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.data.api.model.dto.VisitorDTO
import com.vsu.test.data.api.model.request.CreateVisitorRequest
import com.vsu.test.data.api.model.request.SignUpRequest
import com.vsu.test.data.api.model.request.UpdateProfileCoordinatesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VisitorService {
    @POST("/api/visitors")
    suspend fun createVisitor(@Body body: CreateVisitorRequest): Response<VisitorDTO>

    @PUT("/api/visitors/{id}")
    suspend fun updateEndTimeVisitor(@Path("idVisitor") id: Long): Response<VisitorDTO>
}