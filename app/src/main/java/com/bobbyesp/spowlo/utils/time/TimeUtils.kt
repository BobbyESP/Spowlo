package com.bobbyesp.spowlo.utils.time

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
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

    //Zulu time to local time parser
    @SuppressLint("SimpleDateFormat")
    fun parseDateStringToLocalTime(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        return try {
            val parsedDate = inputFormat.parse(dateString)
            val localTimeString = outputFormat.format(parsedDate!!)
            localTimeString
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}