package com.bobbyesp.spowlo.data.local.model

import android.net.Uri
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song

data class Album(
    val id: Long,
    val artworkUri: Uri,
    val name: String,
    val artist: String,
    val songs: List<Song>
)
