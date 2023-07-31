package com.bobbyesp.spowlo.features.lyrics_downloader.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.bobbyesp.spowlo.features.lyrics_downloader.data.remote.dto.SyncedLinesResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity
data class LyricsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val url: String,
    val lyricsResponse: SyncedLinesResponse
)

class LyricsEntityConverters {
    @TypeConverter
    fun fromSyncedLinesResponse(value: SyncedLinesResponse?): String? {
        return value?.let { Json.encodeToString<SyncedLinesResponse>(it) }
    }

    @TypeConverter
    fun toSyncedLinesResponse(value: String?): SyncedLinesResponse? {
        return value?.let { Json.decodeFromString<SyncedLinesResponse>(it) }
    }
}
