package com.bobbyesp.spowlo.db.entity.artist

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bobbyesp.utilities.utilities.StringUtils
import com.bobbyesp.utilities.utilities.Time
import kotlinx.datetime.LocalDateTime

@Immutable
@Entity(tableName = "artist")
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val thumbnailUrl: String? = null,
    val lastUpdateTime: LocalDateTime = Time.getTimeNowKotlin(),
    val bookmarkedAt: LocalDateTime? = null,
) {
    val isYouTubeArtist: Boolean
        get() = id.startsWith("UC")

    val isLocalArtist: Boolean
        get() = id.startsWith("LA")

    fun toggleLike() = copy(
        bookmarkedAt = if (bookmarkedAt != null) null else Time.getTimeNowKotlin()
    )

    companion object {
        fun generateArtistId() = "LA" + StringUtils.generateRandomString(
            length = 8,
            useLetters = true,
            useNumbers = false
        )
    }
}
