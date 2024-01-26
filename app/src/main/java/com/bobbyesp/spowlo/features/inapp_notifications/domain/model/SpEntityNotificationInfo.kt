package com.bobbyesp.spowlo.features.inapp_notifications.domain.model

import androidx.compose.runtime.Immutable
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType

@Immutable
data class SpEntityNotificationInfo(
    val name: String,
    val artist: String,
    val artworkUrl: String? = null,
    val downloadUrl: String? = null,
    val itemType: SpotifyItemType
)
