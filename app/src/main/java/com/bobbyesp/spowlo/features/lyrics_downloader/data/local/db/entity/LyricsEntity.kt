package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.SyncedLinesResponse

@Entity
data class LyricsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val url: String,
    val lyricsResponse: SyncedLinesResponse
)
