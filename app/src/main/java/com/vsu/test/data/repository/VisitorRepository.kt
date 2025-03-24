package com.vsu.test.data.repository

import com.vsu.test.data.api.VisitorService
import com.vsu.test.data.api.model.dto.VisitorDTO
import com.vsu.test.data.api.model.request.CreateVisitorRequest
import com.vsu.test.utils.BaseApiResponse
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class VisitorRepository @Inject constructor(
    private val visitorService: VisitorService
): BaseApiResponse() {

    suspend fun createEvent(profileId: Long, lightRoomId: Long, visitorId: Long?): NetworkResult<VisitorDTO>{
        val visitorRequest = CreateVisitorRequest(profileId, lightRoomId, visitorId)
        return safeApiCall { visitorService.createVisitor(visitorRequest) }
    }
    suspend fun updateEndTimeVisitor(visitorId: Long): NetworkResult<VisitorDTO>{
        return safeApiCall { visitorService.updateEndTimeVisitor(visitorId) }
    }
}