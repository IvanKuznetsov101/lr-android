package com.vsu.test.domain.model

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO

data class EventWithDetails(
    val event: EventDTO,
    val lightRoom: LightRoomDTO,
    val eventImagesUrls: List<String>,
    val isHere: Boolean,
    val visitorsCount: Long,
    val profileWithDetails: ProfileWithDetails
)