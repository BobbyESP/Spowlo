package com.bobbyesp.spowlo.db.entity.playlist

import androidx.room.Embedded
import androidx.room.Relation
import com.bobbyesp.spowlo.db.entity.Song
import com.bobbyesp.spowlo.db.entity.song.SongEntity

data class PlaylistSong(
    @Embedded val map: PlaylistSongMap,
    @Relation(
        parentColumn = "songId",
        entityColumn = "id",
        entity = SongEntity::class
    )
    val song: Song,
)
