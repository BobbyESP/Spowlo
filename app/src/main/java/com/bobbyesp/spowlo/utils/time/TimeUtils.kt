package com.bobbyesp.spowlo.utils.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Locale

object TimeUtils {
    fun getDateWithTimeAsString(): String {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    }

    fun formatDuration(duration: Long): String {
        val minutes: Long = duration / 60000
        val seconds: Long = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Zulu time to local time parser
    fun parseDateStringToLocalTime(dateString: String): String? {
        val timeZoneUTC = TimeZone.UTC

        return try {
            val parsedDate: Instant = Instant.parse(dateString)
            val localDateTime = parsedDate.toLocalDateTime(timeZoneUTC)
            localDateTime.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun String.calculateTimeDifference(): String {
    val timeDifference = System.currentTimeMillis() - toTimestampInMillis()
    val seconds = timeDifference / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}d"
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
    }
}

fun String.toTimestampInMillis(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")

    return try {
        val date = dateFormat.parse(this)
        date?.time ?: 0
    } catch (e: Exception) {
        val dateFormatWithoutMillis =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormatWithoutMillis.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val dateWithoutMillis = dateFormatWithoutMillis.parse(this)
        dateWithoutMillis?.time ?: 0
    }
}
