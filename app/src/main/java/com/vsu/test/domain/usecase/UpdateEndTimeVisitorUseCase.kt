package com.vsu.test.domain.usecase

import com.vsu.test.data.repository.VisitorRepository
import javax.inject.Inject

class UpdateEndTimeVisitorUseCase @Inject constructor(
private val visitorRepository: VisitorRepository
) {
    suspend fun invoke(visitorId: Long) =
        visitorRepository.updateEndTimeVisitor(visitorId)
}