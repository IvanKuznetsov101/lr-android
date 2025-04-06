package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.VisitorDTO
import com.vsu.test.data.api.model.request.CreateVisitorRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VisitorService {
    @POST("/api/visitors")
    suspend fun createVisitor(@Body body: CreateVisitorRequest): Response<VisitorDTO>

    @PUT("/api/visitors/{id}")
    suspend fun updateEndTimeVisitor(@Path("idVisitor") id: Long): Response<VisitorDTO>

    @GET("/api/visitors/profile/{id}")
    suspend fun getVisitorByProfileId(@Path("id") id: Long): Response<VisitorDTO>

    @GET("/api/visitors/lightroom/{id}")
    suspend fun getVisitorCountByLightRoomId(@Path("id") id: Long): Response<Long>
}