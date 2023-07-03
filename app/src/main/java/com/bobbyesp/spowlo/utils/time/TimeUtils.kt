package com.bobbyesp.spowlo.utils.time

object TimeUtils {
    fun getDateWithTimeAsString(): String {
        val date = java.util.Calendar.getInstance().time
        return date.toString()
    }

}