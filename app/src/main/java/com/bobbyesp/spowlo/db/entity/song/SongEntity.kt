package com.bobbyesp.spowlo.db.entity.song

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bobbyesp.utilities.utilities.Time
import kotlinx.datetime.LocalDateTime

@Immutable
@Entity(
    tableName = "song",
    indices = [
        Index(
            value = ["albumId"]
        )
    ]
)
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val duration: Int = -1, // in seconds
    val thumbnailUrl: String? = null,
    val albumId: String? = null,
    val albumName: String? = null,
    val liked: Boolean = false,
    val totalPlayTime: Long = 0, // in milliseconds
    val inLibrary: LocalDateTime? = null,
) {
    fun toggleLike() = copy(
        liked = !liked,
        inLibrary = if (!liked) inLibrary ?: Time.getTimeNowKotlin() else inLibrary
    )

    fun toggleLibrary() = copy(inLibrary = if (inLibrary == null) Time.getTimeNowKotlin() else null)
}
