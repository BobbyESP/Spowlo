package com.bobbyesp.spowlo.db.entity

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import com.bobbyesp.spowlo.db.entity.artist.ArtistEntity

@Immutable
data class Artist(
    @Embedded
    val artist: ArtistEntity,
    val songCount: Int,
) : LocalItem() {
    override val id: String
        get() = artist.id
}
