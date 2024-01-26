package com.bobbyesp.spowlo.features.spotifyApi.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MetadataEntity(
    val type: SpotifyItemType = SpotifyItemType.TRACKS,
    val id: String
): Parcelable
