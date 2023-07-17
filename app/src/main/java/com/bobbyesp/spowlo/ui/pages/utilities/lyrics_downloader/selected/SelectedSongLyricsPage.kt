package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected

import SpotifyHorizontalSongCard
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.storage.StorageHelper.SaveLyricsButton
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.buttons.CloseButton
import com.bobbyesp.spowlo.ui.components.buttons.DynamicButton
import com.bobbyesp.spowlo.ui.components.buttons.ListenOnSpotifyFilledButton
import com.bobbyesp.spowlo.ui.components.cards.CardListItem
import com.bobbyesp.spowlo.ui.components.cards.WarningCard
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.utils.GeneralTextUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedSongLyricsPage(
    viewModel: SelectedSongLyricsPageViewModel,
    songName: String,
    artistName: String,
) {
    val navController = LocalNavController.current

    val uriOpener = LocalUriHandler.current
    val context = LocalContext.current

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value
    val pageStage = viewModel.pageViewState.collectAsStateWithLifecycle().value.pageStage
    val paginatedTracks = viewState.tracks.collectAsLazyPagingItems()

    val query = "$songName $artistName"

    LaunchedEffect(true) {
        viewModel.getTrackPagingData(query, null)
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(navigationIcon = {
                DynamicButton(icon = {
                    CloseButton {
                        navController.popBackStack()
                    }
                }, icon2 = {
                    BackButton {
                        viewModel.clearSelectedSong()
                    }
                }, isIcon1 = pageStage is PageStage.Selecting
                )
            }, title = {
                Text(
                    text = stringResource(id = R.string.select_song),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            })
        }, modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Crossfade(
            targetState = pageStage, animationSpec = tween(175), label = "Fade between pages"
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
                                    id = R.string.showing_results_for, songName, artistName
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        items(
                            count = paginatedTracks.itemCount,
                            key = paginatedTracks.itemKey(),
                            contentType = paginatedTracks.itemContentType(
                            )
                        ) { index ->
                            val item = paginatedTracks[index]
                            SpotifyHorizontalSongCard(song = item) {
                                viewModel.selectSong(item!!)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        loadStateContent(paginatedTracks)
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
                        Crossfade(
                            targetState = viewState.state,
                            animationSpec = tween(175),
                            label = "Fade between lyrics states"
                        ) { lyricsState ->
                            when (lyricsState) {
                                is SelectedSongLyricsPageState.Loading -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        LinearProgressIndicator(
                                            modifier = Modifier.width(200.dp)
                                        )
                                    }
                                }

                                is SelectedSongLyricsPageState.Loaded -> {

                                    var lyrics by rememberSaveable(key = "lyrics") {
                                        mutableStateOf("")
                                    }

                                    LaunchedEffect(true) {
                                        lyrics = lyricsState.lyrics
                                    }

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(),
                                    ) {
                                        item {
                                            if (selectedSong != null) SpotifyHorizontalSongCard(
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp,
                                                    vertical = 8.dp
                                                ), song = selectedSong!!
                                            )
                                        }
                                        item {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(
                                                    vertical = 4.dp,
                                                    horizontal = 12.dp
                                                )
                                            )
                                        }
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                ListenOnSpotifyFilledButton(
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .padding(end = 12.dp),
                                                ) {
                                                    uriOpener.openUri(selectedSong!!.path)
                                                }
                                            }
                                        }

                                        item {
                                            OutlinedCard(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                shape = MaterialTheme.shapes.extraSmall,
                                                colors = CardDefaults.outlinedCardColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                                        alpha = 0.3f
                                                    ),
                                                )
                                            ) {
                                                SelectionContainer {
                                                    Text(
                                                        text = lyrics,
                                                        modifier = Modifier.padding(8.dp),
                                                        fontWeight = FontWeight.W400
                                                    )
                                                }
                                            }

                                        }
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                SaveLyricsButton(
                                                    modifier = Modifier.weight(0.5f),
                                                    song = selectedSong!!,
                                                    lyrics = lyrics,
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                CardListItem(
                                                    modifier = Modifier.weight(0.5f),
                                                    leadingContentIcon = Icons.Default.CopyAll,
                                                    headlineContentText = stringResource(id = R.string.copy_lyrics)
                                                ) {
                                                    GeneralTextUtils.copyToClipboardAndNotify(
                                                        context,
                                                        lyrics
                                                    )
                                                }
                                            }
                                        }
                                        item {
                                            Spacer(modifier = Modifier.height(16.dp))
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