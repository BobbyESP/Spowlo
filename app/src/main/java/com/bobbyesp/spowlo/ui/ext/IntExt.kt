package com.bobbyesp.spowlo.ui.ext

import com.adamratzman.spotify.endpoints.client.ClientPersonalizationApi

fun Int.toTimeRange(): ClientPersonalizationApi.TimeRange {
    return when (this) {
        0 -> ClientPersonalizationApi.TimeRange.ShortTerm
        1 -> ClientPersonalizationApi.TimeRange.MediumTerm
        2 -> ClientPersonalizationApi.TimeRange.LongTerm
        else -> ClientPersonalizationApi.TimeRange.ShortTerm
    }
}

fun Int.bigQuantityFormatter(): String {
    return when (this) {
        in 0..999 -> this.toString()
        in 1000..999999 -> "${this / 1000} K"
        in 1000000..999999999 -> "${this / 1000000} M"
        else -> "${this / 1000000000} B"
    }
}