package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtPath: Uri? = null,
    val duration: Double,
    val path: String
)
