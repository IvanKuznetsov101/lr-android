package com.vsu.test.data.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ImageService {
    @Multipart
    @POST("/api/images/upload")
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>,
        @Query("profileId") profileId: Long?,
        @Query("eventId") eventId: Long?
    ): Response<String>

    @Multipart
    @PUT("/api/images/update")
    suspend fun updateImages(
        @Part images: List<MultipartBody.Part>,
        @Query("profileId") profileId: Long?,
        @Query("eventId") eventId: Long?
    ): Response<String>

    @DELETE("/api/images/{ids}")
    suspend fun deleteImages(@Path("ids") ids: String): Response<List<Long>>
    @GET("/api/images/{imageId}")
    suspend fun downloadImage(@Path("imageId") imageId: Long): Response<String>

    @GET("/api/images/profile/{profileId}")
    suspend fun getImagesByProfile(@Path("profileId") profileId: Long): Response<String>

    @GET("/api/images/event/{eventId}?links")
    suspend fun getImagesUrlsByEvent(@Path("eventId") eventId: Long): Response<List<String>>

}
