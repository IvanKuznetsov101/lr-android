package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.VisitorDTO
import com.vsu.test.data.repository.VisitorRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetCurrentVisitorByProfileIdUseCase @Inject constructor(

    private val visitorRepository: VisitorRepository
) {
    suspend fun invoke(profileId: Long): VisitorDTO? {
        val response = visitorRepository.getCurrentVisitorByProfileId(profileId)
        if (response is NetworkResult.Success && response.data != null) {
            return response.data
        }
        return null
    }
}