package com.bobbyesp.spowlo.db.entity

import androidx.compose.runtime.Immutable
import com.bobbyesp.spowlo.db.entity.song.SongEntity
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.utils.resize
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class LocalMediaMetadata(
    val id: String,
    val title: String,
    val artists: List<Artist>,
    val duration: Int,
    val thumbnailUrl: String? = null,
    val album: Album? = null,
) {
    @Serializable
    data class Artist(
        val id: String?,
        val name: String,
    )
    @Serializable
    data class Album(
        val id: String,
        val title: String,
    )

    fun toSongEntity() = SongEntity(
        id = id,
        title = title,
        duration = duration,
        thumbnailUrl = thumbnailUrl,
        albumId = album?.id,
        albumName = album?.title
    )
}

fun Song.toMediaMetadata() = LocalMediaMetadata(
    id = song.id,
    title = song.title,
    artists = artists.map {
        LocalMediaMetadata.Artist(
            id = it.id,
            name = it.name
        )
    },
    duration = song.duration,
    thumbnailUrl = song.thumbnailUrl,
    album = album?.let {
        LocalMediaMetadata.Album(
            id = it.id,
            title = it.title
        )
    } ?: song.albumId?.let { albumId ->
        LocalMediaMetadata.Album(
            id = albumId,
            title = song.albumName.orEmpty()
        )
    }
)

fun SongItem.toMediaMetadata() = LocalMediaMetadata(
    id = id,
    title = title,
    artists = artists.map {
        LocalMediaMetadata.Artist(
            id = it.id,
            name = it.name
        )
    },
    duration = duration ?: -1,
    thumbnailUrl = thumbnail.resize(544, 544),
    album = album?.let {
        LocalMediaMetadata.Album(
            id = it.id,
            title = it.name
        )
    }
)