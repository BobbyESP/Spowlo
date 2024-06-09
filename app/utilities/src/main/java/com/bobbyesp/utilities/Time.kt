package com.bobbyesp.utilities

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

object Time {
    fun getZuluTimeSnapshot(): String {
        val instant = Clock.System.now()
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    }

    fun formatDuration(duration: Long): String {
        val minutes: Long = duration / 60000
        val seconds: Long = (duration % 60000) / 1000
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}