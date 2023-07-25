package com.bobbyesp.spowlo.ui.ext

import com.adamratzman.spotify.endpoints.client.ClientPersonalizationApi

fun Int.toTimeRange(): ClientPersonalizationApi.TimeRange {
    return when(this) {
        0 -> ClientPersonalizationApi.TimeRange.ShortTerm
        1 -> ClientPersonalizationApi.TimeRange.MediumTerm
        2 -> ClientPersonalizationApi.TimeRange.LongTerm
        else -> ClientPersonalizationApi.TimeRange.ShortTerm
    }
}