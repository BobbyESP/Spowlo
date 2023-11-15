package com.bobbyesp.piped_lib.domain.model.stream


import kotlinx.serialization.Serializable

@Serializable
data class Stream(
    val audioStreams: List<AudioStream> = listOf(),
    val category: String = "",
    val chapters: List<Chapter> = listOf(),
    val dash: String? = null,
    val description: String = "",
    val dislikes: Int = 0,
    val duration: Int = 0,
    val hls: String = "",
    val lbryId: String? = null,
    val license: String = "",
    val likes: Int = 0,
    val livestream: Boolean = false,
    val metaInfo: List<Nothing> = listOf(), //<-- WE DON'T KNOW WHAT THIS IS, NO INFO FOUND
    val previewFrames: List<PreviewFrame> = listOf(),
    val proxyUrl: String = "",
    val relatedStreams: List<RelatedStream> = listOf(),
    val subtitles: List<Subtitle> = listOf(),
    val tags: List<String> = listOf(),
    val thumbnailUrl: String = "",
    val title: String = "",
    val uploadDate: String = "",
    val uploader: String = "",
    val uploaderAvatar: String = "",
    val uploaderSubscriberCount: Int = 0,
    val uploaderUrl: String = "",
    val uploaderVerified: Boolean = false,
    val videoStreams: List<VideoStream> = listOf(),
    val views: Int = 0,
    val visibility: String = ""
)