package com.bobbyesp.piped_lib.domain.model.stream


import kotlinx.serialization.Serializable

@Serializable
data class AudioStream(
    val audioTrackId: String? = null,
    val audioTrackLocale: String? = null,
    val audioTrackName: String? = null,
    val audioTrackType: String? = null,
    val bitrate: Int = 0,
    val codec: String = "",
    val contentLength: Int = 0,
    val format: String = "",
    val fps: Int = 0,
    val height: Int = 0,
    val indexEnd: Int = 0,
    val indexStart: Int = 0,
    val initEnd: Int = 0,
    val initStart: Int = 0,
    val itag: Int = 0,
    val mimeType: String = "",
    val quality: String = "",
    val url: String = "",
    val videoOnly: Boolean = false,
    val width: Int = 0
)