package com.vsu.test.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object TimeUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

    fun formatTimeDifference(end: String): String {
        return try {
            val startDateTime = LocalDateTime.now(ZoneOffset.UTC) // Текущее время в UTC
            val endDateTime = LocalDateTime.parse(fixNanoFormat(end), formatter)

            val duration = Duration.between(startDateTime, endDateTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60

            "${hours} ч ${minutes} минут"
        } catch (e: Exception) {
            "Ошибка формата даты"
        }
    }

    fun formatTimeDifferenceNow(end: String): String {
        return try {
            val startDateTime = LocalDateTime.now(ZoneOffset.UTC) // Текущее время в UTC
            val endDateTime = LocalDateTime.parse(fixNanoFormat(end), formatter)

            val duration = Duration.between(endDateTime, startDateTime)
            val days = duration.toDays()

            "${days} days"
        } catch (e: Exception) {
            "Ошибка формата даты"
        }
    }

    private fun fixNanoFormat(dateTime: String): String {
        val parts = dateTime.split(".")
        return if (parts.size == 2 && parts[1].length < 6) {
            "${parts[0]}.${parts[1].padEnd(6, '0')}" // Дополняем до 6 знаков
        } else {
            dateTime
        }
    }
}