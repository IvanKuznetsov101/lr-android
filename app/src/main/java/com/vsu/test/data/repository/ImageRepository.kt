package com.vsu.test.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.vsu.test.data.api.ImageService
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Error
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val imageService: ImageService,
    private val contentResolver: ContentResolver
) : BaseApiResponse() {
    suspend fun getImagesUrlsByEventId(eventId: Long): NetworkResult<List<String>> {
        return safeApiCall { imageService.getImagesUrlsByEvent(eventId) }
    }

    suspend fun getImageUrlByProfileId(profileId: Long): NetworkResult<String> {
        return safeApiCall { imageService.getImagesByProfile(profileId) }
    }

    suspend fun uploadImages(
        images: List<Uri>,
        profileId: Long?,
        eventId: Long?
    ): NetworkResult<String> {
        val parts = images.mapNotNull { uri ->
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val requestBody =
                    inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "images",
                    "image_${System.currentTimeMillis()}.jpg",
                    requestBody
                )
            }
        }
        return safeApiCall { imageService.uploadImages(parts, profileId, eventId) }
    }

    suspend fun updateImages(
        images: List<Uri>,
        profileId: Long?,
        eventId: Long?
    ): Any {
        val parts = images.mapNotNull { uri ->
            try {

                val fileName = getFileNameFromUri(uri) ?: "image_${System.currentTimeMillis()}"
                val mimeType = contentResolver.getType(uri) ?: "image/*"
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val requestBody = inputStream.readBytes().toRequestBody(
                        mimeType.toMediaTypeOrNull()
                    )

                    MultipartBody.Part.createFormData(
                        "images",
                        fileName,
                        requestBody
                    )
                }
            } catch (e: Exception) {
                Log.e("UPLOAD", "Error processing $uri", e)
                null
            }
        }

        return if (parts.isNotEmpty()) {
            safeApiCall {
                imageService.updateImages(parts, profileId, eventId)
            }
        } else {
            Error("No valid files selected")
        }
    }

    suspend fun deleteImages(ids: List<Long>): NetworkResult<List<Long>> {
        val stringIds = ids.joinToString(",")
        return safeApiCall { imageService.deleteImages(stringIds) }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                        } else null
                    }
            }

            "file" -> uri.lastPathSegment
            else -> null
        }
    }

}