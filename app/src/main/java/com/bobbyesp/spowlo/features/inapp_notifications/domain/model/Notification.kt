package com.bobbyesp.spowlo.features.inapp_notifications.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import kotlin.random.Random

enum class DURATION(val duration: Long) {
    SHORT(3000),
    MEDIUM(4000),
    LONG(8000),
}

@Stable
data class Notification(
    val id: Int = Random.nextInt(),
    val title: String,
    val subtitle: String,
    val duration: DURATION = DURATION.MEDIUM,
    val timestamp: Long = System.currentTimeMillis(),
    val entityInfo: SpEntityNotificationInfo? = null,
    val content: @Composable (() -> Unit)? = null,
) {
    companion object {
        fun Downloader.DownloadInfo.toNotification(): Notification = with(this) {
            Notification(
                title = title,
                subtitle = artist,
                timestamp = System.currentTimeMillis(),
                entityInfo = SpEntityNotificationInfo(
                    name = title,
                    artist = artist,
                    artworkUrl = thumbnailUrl,
                    downloadUrl = url,
                    itemType = SpotifyItemType.TRACKS,
                ),
            )
        }

        fun from(
            title: String,
            subtitle: String,
            duration: DURATION = DURATION.MEDIUM,
            timestamp: Long = System.currentTimeMillis(),
            entityInfo: SpEntityNotificationInfo? = null,
            content: @Composable (() -> Unit)? = null,
        ) = Notification(
            title = title,
            subtitle = subtitle,
            duration = duration,
            timestamp = timestamp,
            entityInfo = entityInfo,
            content = content,
        )
    }
}
