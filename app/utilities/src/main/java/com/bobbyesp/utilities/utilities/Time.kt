package com.bobbyesp.utilities.utilities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Time {
    fun getZuluTimeSnapshot(): String {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    }

    fun getZuluTime(timestamp: Long): String {
        val instant = timestamp.toInstant()
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    }

    fun getDate(timestamp: Long): String {
        val localDateTime = timestamp.toLocalDateTime()
        val day = localDateTime.dayOfMonth.toString()
        val month = localDateTime.month.name.lowercase(Locale.getDefault())
        val year = localDateTime.year.toString()
        return "$day $month $year"
    }

    fun getTime(timestamp: Long): String {
        val localDateTime = timestamp.toLocalDateTime()
        val hour = localDateTime.hour.toString()
        val minute = localDateTime.minute.toString()
        val second = localDateTime.second.toString()
        return "$hour:$minute:$second"
    }

    fun convertTimestampToDateString(timestamp: Long): String {
        // Create a SimpleDateFormat object with the desired date format
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Convert the timestamp to a Date object
        val date = Date(timestamp)

        // Use the SimpleDateFormat to format the Date object as a string
        return sdf.format(date)
    }


    fun getFormattedDate(timestamp: Long): String {
        val localDateTime = timestamp.toLocalDateTime()
        val date = getDate(timestamp)
        val time = getTime(timestamp)
        //AM or PM
        val amOrPm = if (localDateTime.hour < 12) "AM" else "PM"
        return "$date $time$amOrPm"
    }

    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val localDateTime1 = timestamp1.toLocalDateTime()
        val localDateTime2 = timestamp2.toLocalDateTime()
        return localDateTime1.dayOfMonth == localDateTime2.dayOfMonth &&
                localDateTime1.month == localDateTime2.month &&
                localDateTime1.year == localDateTime2.year
    }

    fun formatDuration(duration: Long): String {
        val minutes: Long = duration / 60000
        val seconds: Long = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

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

    object Localized {
        fun getDate(timestamp: Long): String {
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("d MMMM y", Locale.getDefault())
            return dateFormat.format(date)
        }

        fun getFormattedDate(timestamp: Long): String {
            val localDateTime = timestamp.toLocalDateTime()
            val date = getDate(timestamp)
            val time = getTime(timestamp)
            //AM or PM
            val amOrPm = if (localDateTime.hour < 12) "AM" else "PM"
            return "$date $time$amOrPm"
        }
    }

    fun getTimeNow(): java.time.LocalDateTime {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
    }

    fun getTimeNowKotlin(): LocalDateTime {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
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

fun Int.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this.toLong())
}

fun Long.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(this)
}

fun Long.toLocalDateTime(): LocalDateTime {
    val instant = this.toInstant()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.toLong(): Long {
    return this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}