package com.bobbyesp.spowlo.data.local.model

import android.net.Uri
import android.os.Parcelable
import com.bobbyesp.spowlo.features.lyrics_downloader.domain.model.UriSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SelectedSong(
    val name: String,
    val mainArtist: String,
    val localSongPath: String? = null,
    @Serializable(with = UriSerializer::class) val artworkPath: Uri? = null,
    val fileName: String? = null
) : Parcelable
