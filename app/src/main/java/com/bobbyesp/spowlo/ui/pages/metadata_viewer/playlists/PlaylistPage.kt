package com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.pages.commonPages.LoadingPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.SpotifyPageBinder
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.binders.typeOfSpotifyDataType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistPage(
    onBackPressed: () -> Unit,
    playlistPageViewModel: PlaylistPageViewModel = hiltViewModel(),
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
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    , topBar = {
                    TopAppBar(title = {
                        Text(
                            text = stringResource(id = R.string.metadata_viewer),
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                        )
                    }, navigationIcon = {
                        BackButton { onBackPressed() }
                    }, actions = {}, scrollBehavior = scrollBehavior
                    )
                }) { paddings ->
                    SpotifyPageBinder(
                        data = state.data,
                        type = typeOfSpotifyDataType(type),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddings),
                        trackDownloadCallback = { url, name ->
                            playlistPageViewModel.downloadTrack(url, name)
                        },
                    )

                }
            }
        }
    }
}

sealed class PlaylistDataState {
    object Loading : PlaylistDataState()
    class Error(val error: Exception) : PlaylistDataState()
    class Loaded(val data: Any) : PlaylistDataState()
}