package com.bobbyesp.spowlo.ui.pages.metadata_entities.artist

import CompactSpotifyHorizontalSongCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.bottomSheets.track.TrackBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.buttons.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.buttons.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.modifiers.fadingEdge
import com.bobbyesp.spowlo.ui.components.others.own_shimmer.ArtistSectionShimmer
import com.bobbyesp.spowlo.ui.components.others.tags.RoundedTag
import com.bobbyesp.spowlo.ui.components.others.tags.RoundedTagWithIcon
import com.bobbyesp.spowlo.ui.components.text.AutoResizableText
import com.bobbyesp.spowlo.ui.components.text.LargeCategoryTitle
import com.bobbyesp.spowlo.ui.ext.bigQuantityFormatter
import com.bobbyesp.spowlo.utils.data.Resource
import com.bobbyesp.spowlo.utils.ui.Constants.AppBarHeight
import com.bobbyesp.spowlo.utils.ui.pages.ErrorPage
import com.bobbyesp.spowlo.utils.ui.pages.LoadingPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistPage(
    viewModel: ArtistPageViewModel,
    artistId: String
) {
    LaunchedEffect(artistId) {
        viewModel.loadArtist(artistId)
    }

    val navController = LocalNavController.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true },
    )
    val lazyListState = rememberLazyListState()

    val viewState by viewModel.pageViewState.collectAsStateWithLifecycle()
    val pageState = viewState.state

    val artistTopTracks by remember {
        derivedStateOf {
            viewState.artistTopTracks
        }
    }

    val transparentAppBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    var showTrackSheet by remember {
        mutableStateOf(false)
    }

    Crossfade(
        targetState = pageState,
        modifier = Modifier
            .fillMaxSize(),
        label = "Artist Page Crossfade"
    ) { viewPageState ->
        when (viewPageState) {
            is ArtistPageViewModel.Companion.ArtistPageState.Error -> {
                ErrorPage(error = viewPageState.message) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        viewModel.loadArtist(artistId)
                    }
                }
            }

            ArtistPageViewModel.Companion.ArtistPageState.Loading -> {
                LoadingPage()
            }

            is ArtistPageViewModel.Companion.ArtistPageState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = lazyListState,
                    contentPadding = LocalPlayerAwareWindowInsets.current
                        .add(
                            WindowInsets(
                                top = -WindowInsets.systemBars.asPaddingValues()
                                    .calculateTopPadding() - AppBarHeight
                            )
                        )
                        .asPaddingValues(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item(key = "artist_header") {
                        ArtistHeader(modifier = Modifier, artist = viewPageState.artist)
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    }
                    item(key = "top_tracks_artist") {
                        MostPlayedTracks(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            artistName = viewPageState.artist.name,
                            tracksWithState = artistTopTracks,
                            selectTrack = {
                                viewModel.selectTrackForSheet(it)
                                showTrackSheet = true
                            }
                        )
                    }
                }
                TopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = !transparentAppBar,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Text(text = viewPageState.artist.name)
                        }
                    },
                    navigationIcon = {
                        BackButton {
                            navController.popBackStack()
                        }
                    },
                    actions = {
                    },
                    scrollBehavior = scrollBehavior,
                    colors = if (transparentAppBar) {
                        TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    } else {
                        TopAppBarDefaults.topAppBarColors()
                    }
                )

                if (showTrackSheet && viewState.trackForSheet != null) {
                    TrackBottomSheet(
                        track = viewState.trackForSheet,
                        artworkForSimpleTrack = viewState.trackForSheet!!.album.images.firstOrNull()?.url
                            ?: App.SPOTIFY_LOGO_URL,
                    ) {
                        showTrackSheet = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistHeader(modifier: Modifier, artist: Artist) {
    val artistImage = artist.images.firstOrNull()?.url
    val followersFormatted =
        artist.followers.total?.bigQuantityFormatter() ?: stringResource(id = R.string.unknown)
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3)
                .heightIn(max = 300.dp)
        ) {
            if (artistImage != null) {
                AsyncImageImpl(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fadingEdge(
                            top = WindowInsets.systemBars
                                .asPaddingValues()
                                .calculateTopPadding() + AppBarHeight,
                            bottom = 64.dp
                        )
                        .fillMaxWidth(),
                    model = artistImage,
                    contentDescription = stringResource(R.string.artist),
                    contentScale = ContentScale.FillWidth
                )
            }
            AutoResizableText(
                text = artist.name,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp, top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedTagWithIcon(
                text = followersFormatted + " " + stringResource(id = R.string.followers),
                icon = Icons.Default.Person
            )
            RoundedTagWithIcon(
                text = stringResource(id = R.string.popularity) + ": " + artist.popularity.toString(),
                icon = Icons.Default.StarRate
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            FilledButtonWithIcon(
                modifier = Modifier.weight(1f),
                onClick = { /*TODO*/ },
                icon = Icons.Default.Download,
                text = stringResource(id = R.string.download)
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButtonWithIcon(
                modifier = Modifier.weight(1f),
                onClick = { /*TODO*/ },
                icon = Icons.Default.MoreHoriz,
                text = stringResource(id = R.string.more_options)
            )
        }
        if (artist.genres.isNotEmpty()) {
            LargeCategoryTitle(
                text = stringResource(id = R.string.genres),
                modifier = Modifier.padding(start = 8.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(artist.genres) {
                    RoundedTag(text = it, shape = MaterialTheme.shapes.small)
                }
            }
        }
    }
}

@Composable
private fun MostPlayedTracks(
    modifier: Modifier = Modifier,
    artistName: String,
    tracksWithState: Resource<List<Track>>,
    selectTrack : (Track) -> Unit = {}
) {
    val navController = LocalNavController.current
    Crossfade(
        modifier = modifier,
        targetState = tracksWithState,
        label = "Artist top tracks section crossfade"
    ) { trackList ->
        when (trackList) {
            is Resource.Success -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.top_tracks,
                            artistName
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                            .fadingEdge(bottom = 58.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        trackList.data?.take(6)?.forEachIndexed { index, track ->
                            CompactSpotifyHorizontalSongCard(
                                track = track,
                                modifier = Modifier.height(54.dp),
                                listIndex = index,
                                onClick = {
                                    val selectedMetadataEntity = MetadataEntity(
                                        type = SpotifyItemType.TRACKS,
                                        id = track.id,
                                    )

                                    navController.navigate(
                                        Route.MetadataEntityViewer.createRoute(
                                            selectedMetadataEntity
                                        )
                                    )
                                },
                                onLongClick = {
                                    selectTrack(track)
                                }
                            )
                        }
                    }
                }
            }

            is Resource.Error -> {
                Text(text = trackList.message)
            }

            is Resource.Loading -> {
                ArtistSectionShimmer(
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}