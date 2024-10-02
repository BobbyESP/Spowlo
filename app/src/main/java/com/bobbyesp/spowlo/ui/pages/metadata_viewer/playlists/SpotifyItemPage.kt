package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Playlist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotify_api.model.SpotifyDataType
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.pages.common_pages.LoadingPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.typeOfSpotifyDataType
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.AlbumPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.ArtistPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.PlaylistViewPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.pages.TrackPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyItemPage(
    onBackPressed: () -> Unit,
    playlistPageViewModel: PlaylistPageViewModel = viewModel(),
    id: String,
    type: String,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val viewState by playlistPageViewModel.viewStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        playlistPageViewModel.loadData(id, typeOfSpotifyDataType(type))
    }

    with(viewState) {
        when (this.state) {
            is PlaylistDataState.Loading -> {
                LoadingPage()
            }

            is PlaylistDataState.Error -> {
                Text(text = this.state.error.message.toString())
            }

            is PlaylistDataState.Loaded -> {
                val stateData by remember {
                    mutableStateOf(this.state.data)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.metadata_viewer),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }, navigationIcon = {
                                BackButton { onBackPressed() }
                            }, actions = {}, scrollBehavior = scrollBehavior
                        )
                    }
                ) { paddingValues ->
                    val pageModifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)

                    val trackDownloadCallback: (url: String, name: String) -> Unit = { url, name ->
                        playlistPageViewModel.downloadTrack(url, name)
                    }

                    with(stateData) {
                        when (typeOfSpotifyDataType(type)) {
                            SpotifyDataType.ALBUM -> {
                                val album = this as? Album
                                album?.let {
                                    AlbumPage(album, pageModifier, trackDownloadCallback)
                                }
                            }

                            SpotifyDataType.ARTIST -> {
                                val artist = this as? Artist
                                artist?.let {
                                    ArtistPage(artist, pageModifier, trackDownloadCallback)
                                }
                            }

                            SpotifyDataType.PLAYLIST -> {
                                val playlist = this as? Playlist
                                playlist?.let {
                                    PlaylistViewPage(playlist, pageModifier, trackDownloadCallback)
                                }
                            }

                            SpotifyDataType.TRACK -> {
                                val track = this as? Track
                                track?.let {
                                    TrackPage(track, pageModifier, trackDownloadCallback)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class PlaylistDataState {
    data object Loading : PlaylistDataState()
    class Error(val error: Exception) : PlaylistDataState()
    class Loaded(val data: Any) : PlaylistDataState()
}