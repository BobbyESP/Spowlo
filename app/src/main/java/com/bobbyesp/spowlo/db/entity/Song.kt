package com.bobbyesp.spowlo.db.entity

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.bobbyesp.spowlo.db.entity.album.AlbumEntity
import com.bobbyesp.spowlo.db.entity.artist.ArtistEntity
import com.bobbyesp.spowlo.db.entity.song.SongAlbumMap
import com.bobbyesp.spowlo.db.entity.song.SongEntity
import com.bobbyesp.spowlo.db.entity.song.SortedSongArtistMap

@Immutable
data class Song @JvmOverloads constructor(
    @Embedded val song: SongEntity,
    @Relation(
        entity = ArtistEntity::class,
        entityColumn = "id",
        parentColumn = "id",
        associateBy = Junction(
            value = SortedSongArtistMap::class,
            parentColumn = "songId",
            entityColumn = "artistId"
        )
    )
    val artists: List<ArtistEntity>,
    @Relation(
        entity = AlbumEntity::class,
        entityColumn = "id",
        parentColumn = "id",
        associateBy = Junction(
            value = SongAlbumMap::class,
            parentColumn = "songId",
            entityColumn = "albumId"
        )
    )
    val album: AlbumEntity? = null,
) : LocalItem() {
    override val id: String
        get() = song.id
}
