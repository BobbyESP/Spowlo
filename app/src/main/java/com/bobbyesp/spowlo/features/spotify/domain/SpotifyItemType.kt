package com.bobbyesp.spowlo.features.spotify.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.bobbyesp.spowlo.R

enum class SpotifyItemType {
    TRACKS,
    ALBUMS,
    ARTISTS,
    PLAYLISTS;

    @Composable
    fun toComposableStringPlural(): String {
        return when (this) {
            TRACKS -> pluralStringResource(R.plurals.numberOfTracks, 2)
            ALBUMS -> pluralStringResource(R.plurals.numberOfAlbums, 2)
            ARTISTS -> pluralStringResource(R.plurals.numberOfArtists, 2)
            PLAYLISTS -> pluralStringResource(R.plurals.numberOfPlaylists, 2)
        }
    }

    @Composable
    fun toComposableStringSingular(): String {
        return when (this) {
            TRACKS -> pluralStringResource(R.plurals.numberOfTracks, 1)
            ALBUMS -> pluralStringResource(R.plurals.numberOfAlbums, 1)
            ARTISTS -> pluralStringResource(R.plurals.numberOfArtists, 1)
            PLAYLISTS -> pluralStringResource(R.plurals.numberOfPlaylists, 1)
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