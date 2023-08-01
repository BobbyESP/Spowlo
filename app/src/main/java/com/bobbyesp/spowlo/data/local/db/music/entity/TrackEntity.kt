package com.bobbyesp.spowlo.data.local.db.music.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.adamratzman.spotify.models.PagingObject
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song

/**
 * TrackEntity is a representation of a track from Spotify in the database.
 */
@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val artists: List<String>,
    val album: String,
    val durationMs: Int,
    val url: String? = null,
    val artworkUri: Uri? = null,
    val spotifyId: String? = null
)

class TrackEntityConverters {
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
        id = 1,
        name = name,
        artists = artists.map { it.name },
        album = album.name,
        durationMs = durationMs,
        url = externalUrls.spotify,
        artworkUri = album.images.firstOrNull()?.url?.let { Uri.parse(it) },
        spotifyId = id
    )
}

//List of tracks from Spotify to list of TrackEntity
fun List<Track>.toListOfTrackEntities(): List<TrackEntity> {
    return map { it.toTrackEntity() }
}

//PagingObject of tracks from Spotify to PagingObject of TrackEntity
fun PagingObject<Track>.toTrackEntityPagingObject(): PagingObject<TrackEntity> {
    return PagingObject(
        href = href,
        items = items.toListOfTrackEntities(),
        limit = limit,
        next = next,
        offset = offset,
        previous = previous,
        total = total
    )
}


fun Song.toTrackEntity(): TrackEntity {
    return TrackEntity(
        id = 1,
        name = title,
        artists = listOf(artist),
        album = album,
        durationMs = duration.toInt() * 1000,
        url = path,
        artworkUri = albumArtPath,
        spotifyId = null
    )
}

//List of songs to list of TrackEntity
fun List<Song>.toTrackEntitiesList(): List<TrackEntity> {
    return map { it.toTrackEntity() }
}