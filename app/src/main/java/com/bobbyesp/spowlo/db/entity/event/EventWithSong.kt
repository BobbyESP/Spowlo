package com.bobbyesp.spowlo.db.entity.event

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import com.bobbyesp.spowlo.db.entity.Event
import com.bobbyesp.spowlo.db.entity.Song
import com.bobbyesp.spowlo.db.entity.song.SongEntity

@Immutable
data class EventWithSong(
    @Embedded
    val event: Event,
    @Relation(
        entity = SongEntity::class,
        parentColumn = "songId",
        entityColumn = "id"
    )
    val song: Song,
)
