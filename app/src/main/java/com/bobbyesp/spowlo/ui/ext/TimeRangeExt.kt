package com.bobbyesp.spowlo.ui.ext

import com.adamratzman.spotify.endpoints.client.ClientPersonalizationApi

fun ClientPersonalizationApi.TimeRange.toInt(): Int {
    return when (this) {
        ClientPersonalizationApi.TimeRange.ShortTerm -> 0
        ClientPersonalizationApi.TimeRange.MediumTerm -> 1
        ClientPersonalizationApi.TimeRange.LongTerm -> 2
    }
}