package com.bobbyesp.spowlo.features.spotify_api.data.dtos

import com.adamratzman.spotify.models.ReleaseDate

data class SpotifyData(
    val artworkUrl: String = "",
    val name: String = "",
    val artists: List<String> = emptyList(),
    val releaseDate: ReleaseDate? = null,
    val type: SpotifyDataType = SpotifyDataType.TRACK
)

enum class SpotifyDataType {
    TRACK,
    ALBUM,
    PLAYLIST,
    ARTIST
}