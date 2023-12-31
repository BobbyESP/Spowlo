package com.bobbyesp.spowlo.db.entity.playlist

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.apache.commons.lang3.RandomStringUtils

@Immutable
@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey val id: String = generatePlaylistId(),
    val name: String,
    val browseId: String? = null,
) {
    companion object {
        const val LIKED_PLAYLIST_ID = "LP_LIKED"
        const val DOWNLOADED_PLAYLIST_ID = "LP_DOWNLOADED"

        fun generatePlaylistId() = "LP" + RandomStringUtils.random(8, true, false)
    }
}
