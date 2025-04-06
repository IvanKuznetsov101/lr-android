package com.vsu.test.domain.usecase

import android.net.Uri
import com.vsu.test.data.repository.ImageRepository
import com.vsu.test.presentation.ui.components.CurrentImages
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class UpdateAndDeleteImagesUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(images: CurrentImages.Images, profileId: Long?, eventId: Long?) {
        val imagesToDelete = images.imagesUrls?.filter { it.isDelete } ?: emptyList()
        if (imagesToDelete.isNotEmpty()){
            val idsToDelete = imagesToDelete.map { imageUrl ->
                val url = imageUrl.imageUrl
                val idStr = url.substringAfterLast("/").toLong()
                idStr
            }
            val response =  repository.deleteImages(idsToDelete)
            if (response is NetworkResult.Success){

            }

        }
        val imagesToUpload = images.imagesUris?: emptyList()
        if (imagesToUpload.isNotEmpty()){
            repository.uploadImages(imagesToUpload, profileId, eventId)
        }
    }
}