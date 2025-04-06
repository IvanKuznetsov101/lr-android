package com.vsu.test.domain.usecase

import android.net.Uri
import com.vsu.test.data.repository.ImageRepository
import javax.inject.Inject

class UpdateImagesUseCase @Inject constructor(private val repository: ImageRepository) {
    suspend operator fun invoke(images: List<Uri>, profileId: Long?, eventId: Long?) =
        repository.updateImages(images, profileId, eventId)
}