package com.bobbyesp.spotdl_android.feats.whl_downloader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Downloads(
    @SerialName("last_day") val lastDay: Int,
    @SerialName("last_month") val lastMonth: Int,
    @SerialName("last_week") val lastWeek: Int
)

