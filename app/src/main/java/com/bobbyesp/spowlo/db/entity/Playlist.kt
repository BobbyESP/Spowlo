package com.bobbyesp.spowlo.db.entity

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistEntity
import com.bobbyesp.spowlo.db.entity.playlist.PlaylistSongMapPreview
import com.bobbyesp.spowlo.db.entity.song.SongEntity

@Immutable
data class Playlist(
    @Embedded
    val playlist: PlaylistEntity,
    val songCount: Int,
    @Relation(
        entity = SongEntity::class,
        entityColumn = "id",
        parentColumn = "id",
        projection = ["thumbnailUrl"],
        associateBy = Junction(
            value = PlaylistSongMapPreview::class,
            parentColumn = "playlistId",
            entityColumn = "songId"
        )
    )
    val thumbnails: List<String>,
) : LocalItem() {
    override val id: String
        get() = playlist.id
}
