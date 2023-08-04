package com.bobbyesp.spowlo.ui.pages.metadata_entities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.ALBUMS
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.ARTISTS
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.PLAYLISTS
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType.TRACKS

@Composable
fun MetadataEntityBinder(
    metadataEntity: MetadataEntity
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (metadataEntity.type) {
            TRACKS -> {}
            ALBUMS -> {}
            ARTISTS -> {}
            PLAYLISTS -> {}
        }
    }
}