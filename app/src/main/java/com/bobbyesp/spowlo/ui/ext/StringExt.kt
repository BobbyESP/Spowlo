package com.bobbyesp.spowlo.ui.ext

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.toList(): List<String> {
    val separators = listOf("/", ",")
    return split(*separators.toTypedArray<String>())
}

fun String.getNumbers(): Int {
    return replace("[^0-9]".toRegex(), "").toInt()
}

fun String.getId(): String {
    return split(":").last()
}

fun String.toTimestampInMillis(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val date = dateFormat.parse(this)
    return date?.time ?: 0
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