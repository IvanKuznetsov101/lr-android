package com.vsu.test.data.api.model.dto

import java.time.LocalDate

data class LastEvent(
    var eventId: Long,
    var title: String,
    var profileId: Long,
    var image: String,
    var visitorEndTime: String
)
