package com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SyncedLinesResponse(
    val error: Boolean,
    val syncType: String,
    val lines: List<Line>
)

@Serializable
data class Line(
    val timeTag: String,
    val words: String
)
