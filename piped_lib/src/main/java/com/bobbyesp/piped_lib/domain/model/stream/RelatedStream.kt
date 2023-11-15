package com.bobbyesp.piped_lib.domain.model.stream


import kotlinx.serialization.Serializable

@Serializable
data class RelatedStream(
    val duration: Int = 0,
    val isShort: Boolean = false,
    val shortDescription: String? = null,
    val thumbnail: String = "",
    val title: String = "",
    val type: String = "",
    val uploaded: Long = 0,
    val uploadedDate: String = "",
    val uploaderAvatar: String = "",
    val uploaderName: String = "",
    val uploaderUrl: String = "",
    val uploaderVerified: Boolean = false,
    val url: String = "",
    val views: Int = 0
)