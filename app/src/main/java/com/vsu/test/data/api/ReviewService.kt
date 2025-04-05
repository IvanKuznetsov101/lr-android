package com.vsu.test.data.api

import com.vsu.test.data.api.model.dto.AverageRatingWithCount
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.ReviewDTO
import com.vsu.test.data.api.model.dto.ReviewWithProfile
import com.vsu.test.data.api.model.request.CreateReviewRequest
import com.vsu.test.data.api.model.request.EventRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewService {
    @GET("/api/reviews/profile/{profileId}")
    suspend fun getReviewsByProfileId(@Path("profileId") profileId: Long): Response<List<ReviewWithProfile>>

    @POST("/api/reviews")
    suspend fun createReview(@Body createReviewRequest: CreateReviewRequest): Response<Long>

    @GET("/api/reviews/average/{profileId}")
    suspend fun getAverageRatingWithCountByProfileId(@Path("profileId") profileId: Long): Response<AverageRatingWithCount>

    @GET("/api/reviews/profile/{fromProfileId}/{toProfileId}")
    suspend fun getReviewByProfileIds(@Path("fromProfileId") fromProfileId: Long, @Path("toProfileId") toProfileId: Long,): Response<ReviewWithProfile>
}
