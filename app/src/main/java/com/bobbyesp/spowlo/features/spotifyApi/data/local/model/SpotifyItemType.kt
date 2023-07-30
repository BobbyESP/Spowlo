package com.bobbyesp.spowlo.features.spotifyApi.data.local.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R

enum class SpotifyItemType {
    TRACKS,
    ALBUMS,
    ARTISTS,
    PLAYLISTS;

    @Composable
    fun toComposableStringPlural(): String {
        return when (this) {
            TRACKS -> stringResource(id = R.string.tracks)
            ALBUMS -> stringResource(id = R.string.albums)
            ARTISTS -> stringResource(id = R.string.artists)
            PLAYLISTS -> stringResource(id = R.string.playlists)
        }
    }

    @Composable
    fun toComposableStringSingular(): String {
        return when (this) {
            TRACKS -> stringResource(id = R.string.track)
            ALBUMS -> stringResource(id = R.string.album)
            ARTISTS -> stringResource(id = R.string.artist)
            PLAYLISTS -> stringResource(id = R.string.playlist)
        }
    }
}

fun String.toSpotifyItemType(): SpotifyItemType {
    return when (this) {
        "track" -> SpotifyItemType.TRACKS
        "album" -> SpotifyItemType.ALBUMS
        "artist" -> SpotifyItemType.ARTISTS
        "playlist" -> SpotifyItemType.PLAYLISTS
        else -> throw IllegalArgumentException("String $this is not a valid SpotifyItemType")
    }
}