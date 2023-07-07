package com.bobbyesp.spowlo.utils.time

import java.util.concurrent.TimeUnit

object TimeUtils {
    fun getDateWithTimeAsString(): String {
        val date = java.util.Calendar.getInstance().time
        return date.toString()
    }

    fun formatDuration(duration: Long): String {
        val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds: Long = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }
}