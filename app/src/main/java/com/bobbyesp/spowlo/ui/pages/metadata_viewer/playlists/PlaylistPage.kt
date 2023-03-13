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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.features.spotify_api.data.dtos.SpotifyData
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.pages.common.LoadingPage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPage(
    onBackPressed: () -> Unit,
    playlistPageViewModel: PlaylistPageViewModel = hiltViewModel(),
    id: String
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val viewState by playlistPageViewModel.viewStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        delay(1000)
        playlistPageViewModel.loadData()
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
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(title = {
                            Text(
                                text = "Playlist (WIP)",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                            )
                        }, navigationIcon = {
                            BackButton { onBackPressed() }
                        }, actions = {
                        }, scrollBehavior = scrollBehavior
                        )
                    }) { paddings ->
                    Text(text = this.state.data.toString() + id, modifier = Modifier.padding(paddings))
                }

            }

        }
    }

}

sealed class PlaylistDataState {
    object Loading : PlaylistDataState()
    class Error(val error: Exception) : PlaylistDataState()
    class Loaded(val data: SpotifyData) : PlaylistDataState()
}

class ToolbarOptions(
    val big: Boolean = false,
    val alwaysVisible: Boolean = false
)