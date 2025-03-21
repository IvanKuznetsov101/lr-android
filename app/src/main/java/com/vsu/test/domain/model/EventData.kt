package com.vsu.test.domain.model

data class EventData(
    val id: Long?,
    var title: String?,
    var description: String?,
    var ageLimit: Int?,
    val profileId: Long?
)