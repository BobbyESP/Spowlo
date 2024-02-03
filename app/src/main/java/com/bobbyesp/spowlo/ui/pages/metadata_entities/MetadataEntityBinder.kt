package com.bobbyesp.spowlo.ui.pages.metadata_entities

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.ALBUMS
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.ARTISTS
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.PLAYLISTS
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.TRACKS
import com.bobbyesp.spowlo.ui.pages.metadata_entities.album.AlbumPage
import com.bobbyesp.spowlo.ui.pages.metadata_entities.album.AlbumPageViewModel
import com.bobbyesp.spowlo.ui.pages.metadata_entities.artist.ArtistPage
import com.bobbyesp.spowlo.ui.pages.metadata_entities.artist.ArtistPageViewModel
import com.bobbyesp.spowlo.ui.pages.metadata_entities.playlist.PlaylistPage
import com.bobbyesp.spowlo.ui.pages.metadata_entities.playlist.PlaylistPageViewModel
import com.bobbyesp.spowlo.ui.pages.metadata_entities.track.TrackPage
import com.bobbyesp.spowlo.ui.pages.metadata_entities.track.TrackPageViewModel

@Composable
fun MetadataEntityBinder(
    metadataEntity: MetadataEntity
) {
    when (metadataEntity.type) {
        TRACKS -> {
            val trackViewModel = hiltViewModel<TrackPageViewModel>()
            TrackPage(viewModel = trackViewModel, songId = metadataEntity.id)
        }

        ALBUMS -> {
            val albumViewModel = hiltViewModel<AlbumPageViewModel>()

            AlbumPage(viewModel = albumViewModel, albumId = metadataEntity.id)
        }

        ARTISTS -> {
            val artistViewModel = hiltViewModel<ArtistPageViewModel>()

            ArtistPage(viewModel = artistViewModel, artistId = metadataEntity.id)
        }

        PLAYLISTS -> {
            val playlistViewModel = hiltViewModel<PlaylistPageViewModel>()

            PlaylistPage(viewModel = playlistViewModel, playlistId = metadataEntity.id)
        }
    }
}