package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model

import android.graphics.Bitmap

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumArt: Bitmap? = null,
    val duration: Int,
    val path: String
)
