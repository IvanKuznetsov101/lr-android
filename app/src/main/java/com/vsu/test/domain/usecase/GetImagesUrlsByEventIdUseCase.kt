package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.ImageRepository
import javax.inject.Inject

class GetImagesUrlsByEventIdUseCase @Inject constructor(private val repository: ImageRepository) {
    suspend operator fun invoke(eventId: Long) =
        repository.getImagesUrlsByEventId(eventId)
}