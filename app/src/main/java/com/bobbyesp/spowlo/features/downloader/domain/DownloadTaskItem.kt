package com.bobbyesp.spowlo.features.downloader.domain

import com.bobbyesp.library.dto.Song

data class DownloadTaskItem(
    val info: Song = Song(),
    val spotifyUrl: String = "",
    val name: String = "",
    val artist: String = "",
    val duration: Double = 0.0,
    val isExplicit: Boolean = false,
    val hasLyrics: Boolean = false,
    val progress: Float = 0f,
    val progressText: String = "",
    val thumbnailUrl: String = "",
    val taskId: String = "",
    val output: String = "",
)