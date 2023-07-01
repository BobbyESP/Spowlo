package com.bobbyesp.spotdl_android.feats.whl_downloader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Digests(
    @SerialName("blake2b_256") val blake2b256: String,
    val md5: String,
    val sha256: String
)

