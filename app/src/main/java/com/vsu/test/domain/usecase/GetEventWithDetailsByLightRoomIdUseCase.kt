package com.vsu.test.domain.usecase


import VisitorInfo
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.ImageRepository
import com.vsu.test.data.repository.LightRoomRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.data.repository.ReviewRepository
import com.vsu.test.data.repository.VisitorRepository
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.utils.NetworkResult
import javax.inject.Inject

class GetEventWithDetailsByLightRoomIdUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val profileRepository: ProfileRepository,
    private val lightRoomRepository: LightRoomRepository,
    private val visitorRepository: VisitorRepository,
    private val tokenManager: TokenManager,
    private val imageRepository: ImageRepository,
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(lightRoomDTO: LightRoomDTO): EventWithDetails? {
        val event = getEventByLightRoomId(lightRoomDTO.id)
        val eventImagesUrls = event?.let { getImagesByEventId(it.id) }
        val visitorInfo = getVisitorState(tokenManager.getId())
        val lightRoom = event?.let { getLightRoomByEventId(it.id) }
        val isHere = lightRoom?.id == visitorInfo.lightRoomId
        val count = lightRoom?.let { getVisitorCountByLightRoomId(it.id) }
        val profileWithDetails = event?.let { getProfileWithDetailsByEventId(it.id) }

        if (event == null || visitorInfo == null || lightRoom == null ||
            /*isHere == null ||*/ count == null || profileWithDetails == null
        ) {
            return null
        }
        return EventWithDetails(
            event = event,
            lightRoom = lightRoom,
            isHere = isHere ?: false,
            visitorsCount = count,
            profileWithDetails = profileWithDetails,
            eventImagesUrls = eventImagesUrls
        )
    }

    private suspend fun getLightRoomByEventId(eventId: Long): LightRoomDTO? {
        val response = lightRoomRepository.getLightRoomByEventID(eventId)
        return if (response is NetworkResult.Success) response.data else null
    }

    private suspend fun getImagesByEventId(eventId: Long): List<String>? {
        val response = imageRepository.getImagesUrlsByEventId(eventId)
        return if (response is NetworkResult.Success) response.data else null
    }

    private suspend fun getProfileWithDetailsByEventId(eventId: Long): ProfileWithDetails? {
        val profileResponse = profileRepository.getProfileByEventId(eventId)
        val profileImageUrlResponse =
            profileResponse.data?.let { imageRepository.getImageUrlByProfileId(it.id) }
        val averageRatingWithCountResponse =
            profileResponse.data?.let { reviewRepository.getAverageRatingByProfileId(it.id) }
        return if (profileResponse is NetworkResult.Success && profileResponse.data != null &&
            averageRatingWithCountResponse is NetworkResult.Success && averageRatingWithCountResponse.data != null
        )
            ProfileWithDetails(
                profileResponse.data,
                profileImageUrlResponse?.data.toString(),
                averageRatingWithCountResponse.data
            ) else return null
    }

    private suspend fun getEventByLightRoomId(lightRoomId: Long): EventDTO? {
        val response = eventRepository.getEventByLightRoomId(lightRoomId)
        return if (response is NetworkResult.Success) response.data else null
    }

    private suspend fun getVisitorCountByLightRoomId(lightRoomId: Long): Long {
        val response = visitorRepository.getVisitorCountByLightRoomId(lightRoomId)
        return if (response is NetworkResult.Success) response.data!! else 0L
    }

    private suspend fun getVisitorState(profileId: Long): VisitorInfo {
        val visitorResponse = visitorRepository.getCurrentVisitorByProfileId(profileId)
        if (visitorResponse is NetworkResult.Success) {
            val newVisitorInfo = VisitorInfo(
                visitorId = visitorResponse.data!!.idVisitor,
                lightRoomId = visitorResponse.data.idLightRoom,
                profileId = visitorResponse.data.idProfile
            )
            return newVisitorInfo
        }
        return VisitorInfo(null, null, null)
    }
}