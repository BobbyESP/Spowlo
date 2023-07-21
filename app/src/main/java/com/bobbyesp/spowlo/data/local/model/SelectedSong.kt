package com.bobbyesp.spowlo.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SelectedSong(
    val name: String,
    val mainArtist: String,
    val localSongPath: String? = null,
): Parcelable
