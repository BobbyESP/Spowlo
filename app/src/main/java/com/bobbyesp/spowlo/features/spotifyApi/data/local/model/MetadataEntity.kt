package com.bobbyesp.spowlo.features.spotifyApi.data.local.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class MetadataEntity(
    val type: SpotifyItemType = SpotifyItemType.TRACKS,
    val id: String
): Parcelable
