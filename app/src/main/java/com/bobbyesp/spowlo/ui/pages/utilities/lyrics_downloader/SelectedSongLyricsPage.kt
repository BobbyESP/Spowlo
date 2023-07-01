package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.buttons.CloseButton
import com.bobbyesp.spowlo.ui.components.buttons.DynamicButton
import com.bobbyesp.spowlo.ui.components.cards.WarningCard
import com.bobbyesp.spowlo.ui.components.cards.horizontal.HorizontalSongCard
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedSongLyricsPage(
    viewModel: SelectedSongLyricsPageViewModel,
    songName: String,
    artistName: String,
) {
    val navController = LocalNavController.current

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value

    val pageStage = viewModel.pageViewState.collectAsStateWithLifecycle().value.pageStage

    val query = "$songName $artistName"

    var searchedSongs by rememberSaveable(query) {
        mutableStateOf<List<Song>>(emptyList())
    }

    LaunchedEffect(true) {
        searchedSongs = viewModel.searchSongOnSpotify(query)
    }


    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    DynamicButton(
                        icon = {
                            CloseButton {
                                navController.popBackStack()
                            }
                        },
                        icon2 = {
                            BackButton {
                                viewModel.clearSelectedSong()
                            }
                        },
                        isIcon1 = pageStage is PageStage.Selecting
                    )
                }, title = {
                    Text(
                        text = stringResource(id = R.string.select_song),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                })
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Crossfade(
            targetState = pageStage,
            animationSpec = tween(175),
            label = "Fade between pages"
        ) { actualPageState ->
            when (actualPageState) {
                is PageStage.Selecting -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            WarningCard(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 8.dp),
                                title = stringResource(id = R.string.warning),
                                warningText = stringResource(
                                    id = R.string.lyrics_downloader_warning_text
                                )
                            )
                        }

                        item {
                            Text(
                                text = stringResource(
                                    id = R.string.showing_results_for,
                                    songName,
                                    artistName
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        items(searchedSongs) { song ->
                            HorizontalSongCard(song = song) {
                                viewModel.selectSong(song)
                            }

                            //while it isn't the last item, add a spacer
                            if (searchedSongs.indexOf(song) != searchedSongs.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                }

                is PageStage.Selected -> {
                    BackHandler {
                        viewModel.clearSelectedSong()
                    }

                    var selectedSong by rememberSaveable(key = "selectedSong") {
                        mutableStateOf<Song?>(null)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LaunchedEffect(true) {
                            selectedSong = viewState.selectedSong
                            viewModel.getLyrics(viewState.selectedSong!!.path)
                        }
                        if(selectedSong != null) HorizontalSongCard(song = selectedSong!!)

                        Crossfade(
                            targetState = viewState.state,
                            animationSpec = tween(175),
                            label = "Fade between lyrics states"
                        ) { lyricsState ->
                            when(lyricsState){
                                is SelectedSongLyricsPageState.Loading -> {
                                    Text(text = "Loading...")
                                }
                                is SelectedSongLyricsPageState.Loaded -> {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(),
                                    ) {
                                        item {
                                            Text(text = lyricsState.lyrics, modifier = Modifier.padding(8.dp))
                                        }
                                    }
                                }
                                is SelectedSongLyricsPageState.Error -> {
                                    Text(text = lyricsState.error)
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}