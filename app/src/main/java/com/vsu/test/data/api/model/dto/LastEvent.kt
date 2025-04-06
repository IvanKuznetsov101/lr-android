package com.vsu.test.data.api.model.dto


data class LastEvent(
    var eventId: Long,
    var title: String,
    var profileId: Long,
    var image: String,
    var visitorEndTime: String
)
