package com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.features.spotify_api.model.SpotifyDataType
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.AlbumPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.ArtistPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.PlaylistViewPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.TrackPage

@Composable
fun SpotifyPageBinder(
    data: Any,
    type: SpotifyDataType,
    modifier: Modifier = Modifier,
    trackDownloadCallback : (String) -> Unit,
) {
    Column(modifier = modifier) {
        when (type) {
            SpotifyDataType.ALBUM -> {
                val album = data as? Album
                album?.let {
                    AlbumPage(album, modifier)
                }
            }

            SpotifyDataType.ARTIST -> {
                val artist = data as? Artist
                artist?.let {
                    ArtistPage(artist, modifier)
                }
            }

            SpotifyDataType.PLAYLIST -> {
                val playlist = data as? Playlist
                playlist?.let {
                    PlaylistViewPage(playlist, modifier)
                }
            }

            SpotifyDataType.TRACK -> {
                val track = data as? Track
                track?.let {
                    TrackPage(track,modifier,trackDownloadCallback)
                }
            }
        }
    }
}

//data-type to SpotifyDataType