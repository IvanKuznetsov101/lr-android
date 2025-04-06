package com.vsu.test.domain.usecase

import androidx.compose.ui.text.rememberTextMeasurer
import com.vsu.test.data.repository.ImageRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetImagesUrlsByEventIdUseCase @Inject constructor(private val repository: ImageRepository) {
    suspend operator fun invoke(eventId: Long): List<String> {
        val response = repository.getImagesUrlsByEventId(eventId)
        if (response is NetworkResult.Success && response.data != null){
            return response.data
        }
        return emptyList()
    }
}