package com.bobbyesp.spowlo.db.entity.album

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bobbyesp.utilities.utilities.Time
import kotlinx.datetime.LocalDateTime

@Immutable
@Entity(tableName = "album")
data class AlbumEntity(
    @PrimaryKey val id: String,
    val title: String,
    val year: Int? = null,
    val thumbnailUrl: String? = null,
    val themeColor: Int? = null,
    val songCount: Int,
    val duration: Int,
    val lastUpdateTime: LocalDateTime = Time.getTimeNowKotlin(),
    val bookmarkedAt: LocalDateTime? = null,
) {
    fun toggleLike() = copy(
        bookmarkedAt = if (bookmarkedAt != null) null else Time.getTimeNowKotlin()
    )
}