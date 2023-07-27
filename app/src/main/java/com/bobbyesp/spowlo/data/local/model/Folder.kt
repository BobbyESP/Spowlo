package com.bobbyesp.spowlo.data.local.model

import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song

data class Folder(
    val name: String,
    val songs: List<Song>
)
