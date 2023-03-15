package com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyDataType

//make a composable that has as parameter a SpotifyData and returns the type of the data as a string with a string resource
@Composable
fun typeOfDataToString(type: SpotifyDataType): String {
    return when (type) {
        SpotifyDataType.ALBUM -> stringResource(id = R.string.album)
        SpotifyDataType.ARTIST -> stringResource(id = R.string.artist)
        SpotifyDataType.PLAYLIST -> stringResource(id = R.string.playlist)
        SpotifyDataType.TRACK -> stringResource(id = R.string.track)
    }
}