package com.bobbyesp.spowlo.data.local.db.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song

@Entity
data class TrackEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val artists: List<String>,
    val album: String,
    val durationMs: Int,
    val url: String? = null,
    val artworkUri: Uri? = null,
)

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun toString(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun fromUri(value: Uri?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toUri(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }
}


fun Track.toTrackEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        name = name,
        artists = artists.map { it.name },
        album = album.name,
        durationMs = durationMs,
        url = externalUrls.spotify,
        artworkUri = album.images.firstOrNull()?.url?.let { Uri.parse(it) }
    )
}

fun Song.toTrackEntity(): TrackEntity {
    return TrackEntity(
        id = id.toString(),
        name = title,
        artists = listOf(artist),
        album = album,
        durationMs = duration.toInt() * 1000,
        url = path,
        artworkUri = albumArtPath
    )
}