package com.vsu.test.domain.usecase

import com.vsu.test.data.api.model.dto.VisitorDTO
import com.vsu.test.data.repository.VisitorRepository
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class CreateVisitorUseCase @Inject constructor(
    private val visitorRepository: VisitorRepository
) {
    suspend fun invoke(profileId: Long, lightRoomId: Long): NetworkResult<VisitorDTO> {
        return visitorRepository.createVisitor(profileId, lightRoomId)
    }

}