package com.bobbyesp.spowlo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DownloadedSongInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val songName: String,
    val songAuthor: String,
    val songUrl: String,
    val thumbnailUrl: String,
    val songPath: String,
    @ColumnInfo(defaultValue = "0.0")
    val songDuration: Double = 0.0,
    @ColumnInfo(defaultValue = "Unknown")
    val extractor: String = "Unknown"
)