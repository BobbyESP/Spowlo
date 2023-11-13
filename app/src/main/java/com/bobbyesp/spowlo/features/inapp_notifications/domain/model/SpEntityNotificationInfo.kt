package com.bobbyesp.spowlo.features.inapp_notifications.domain.model

import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType

data class SpEntityNotificationInfo(
    val name: String,
    val artist: String,
    val artworkUrl: String? = null,
    val downloadUrl: String? = null,
    val itemType: SpotifyItemType
)
