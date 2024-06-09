package com.bobbyesp.ext

fun Int.bigQuantityFormatter(): String {
    return when (this) {
        in 0..999 -> this.toString()
        in 1000..999999 -> "${this / 1000} K"
        in 1000000..999999999 -> "${this / 1000000} M"
        else -> "${this / 1000000000} B"
    }
}

fun Int.toMinutes(): String {
    return java.text.SimpleDateFormat("mm:ss").format(java.util.Date(this.toLong()))
}