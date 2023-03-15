package com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyData
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyDataType
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.AlbumPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.ArtistPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.PlaylistViewPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.TrackPage

@Composable
fun SpotifyPageBinder(
    spotifyData: SpotifyData,
    modifier : Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (spotifyData.type) {
            SpotifyDataType.ALBUM -> {
                AlbumPage(spotifyData, modifier)
            }

            SpotifyDataType.ARTIST -> {
                ArtistPage(spotifyData, modifier)
            }

            SpotifyDataType.PLAYLIST -> {
                PlaylistViewPage(spotifyData, modifier)
            }

            SpotifyDataType.TRACK -> {
                TrackPage(spotifyData, modifier)
            }
        }
    }
}