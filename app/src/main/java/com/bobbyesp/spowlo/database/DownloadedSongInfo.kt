package com.bobbyesp.spowlo.database

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class DownloadedSongInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val songTitle: String,
    val songArtist: String,
    val songUrl: String,
    val thumbnailUrl: String,
    val songPath: String,
    @ColumnInfo(defaultValue = "Unknown")
    val extractor: String = "Unknown"
)