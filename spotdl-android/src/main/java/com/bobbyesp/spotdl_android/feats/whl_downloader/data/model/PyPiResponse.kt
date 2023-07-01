package com.bobbyesp.spotdl_android.feats.whl_downloader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PyPiResponse(
    val info: Info,
    @SerialName("last_serial") val lastSerial: Int,
    val releases: Map<String, List<Release>>,
    val urls: List<URL>
)

