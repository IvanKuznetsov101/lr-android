package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.VisitorDTO
import com.vsu.test.data.repository.VisitorRepository
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetVisitorStateUseCase @Inject constructor(
    private val visitorRepository: VisitorRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): VisitorDTO? {
        val visitorResponse = visitorRepository.getCurrentVisitorByProfileId(tokenManager.getId())
        if (visitorResponse is NetworkResult.Success && visitorResponse.data != null) {
            return visitorResponse.data
        } else {
            return null
        }
    }
}

