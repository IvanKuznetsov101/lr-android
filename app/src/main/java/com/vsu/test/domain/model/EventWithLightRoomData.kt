package com.vsu.test.domain.model

import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO

data class EventWithLightRoomData(
    val event: EventDTO,
    val lightRoom: LightRoomDTO
)
