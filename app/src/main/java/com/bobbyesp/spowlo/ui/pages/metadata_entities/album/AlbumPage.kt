package com.bobbyesp.spowlo.ui.pages.metadata_entities.album

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImagePainter
import com.adamratzman.spotify.models.Album
import com.bobbyesp.spowlo.App.Companion.SPOTIFY_LOGO_URL
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.bottomSheets.track.TrackBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.buttons.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.buttons.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.components.cards.songs.horizontal.MetadataEntityItem
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.others.own_shimmer.HorizontalSongCardShimmer
import com.bobbyesp.spowlo.ui.components.others.tags.RoundedTag
import com.bobbyesp.spowlo.ui.components.text.LargeCategoryTitle
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.bobbyesp.spowlo.ui.ext.formatArtistsName
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.ui.ext.toCompleteString
import com.bobbyesp.spowlo.utils.ui.pages.ErrorPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionActionable

@Composable
fun AlbumPage(
    viewModel: AlbumPageViewModel,
    albumId: String
) {
    LaunchedEffect(albumId) {
        viewModel.loadAlbum(albumId)
    }

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    BackHandler {
        navController.popBackStack()
    }

    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = viewState.value.state,
        animationSpec = tween(175),
        label = "Album Page Crossfade"
    ) {
        when (it) {
            is AlbumPageViewModel.Companion.AlbumPageState.Error -> {
                ErrorPage(error = it.e) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        viewModel.loadAlbum(albumId)
                    }
                }
            }

            AlbumPageViewModel.Companion.AlbumPageState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AlbumPageViewModel.Companion.AlbumPageState.Success -> {
                AlbumPageImplementation(viewModel = viewModel, loadedState = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumPageImplementation(
    viewModel: AlbumPageViewModel,
    loadedState: AlbumPageViewModel.Companion.AlbumPageState.Success
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value

    val navController = LocalNavController.current

    val albumData = loadedState.album

    var showTrackSheet by remember {
        mutableStateOf(false)
    }

    val artistsString = albumData.artists.formatArtistsName()
    val dominantColor = viewState.dominantColor ?: MaterialTheme.colorScheme.primary

    val albumTracks = viewState.albumTracksPaginated.collectAsLazyPagingItems()

    val lazyColumnState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true },
    )

    val transparentAppBar by remember {
        derivedStateOf {
            lazyColumnState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                modifier = Modifier,
                title = { },
                navigationIcon = {
                    BackButton {
                        navController.popBackStack()
                    }
                },
                colors = if (transparentAppBar) {
                    TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                } else {
                    TopAppBarDefaults.topAppBarColors()
                },
                actions = {

                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumnScrollbar(
            listState = lazyColumnState,
            thumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            thumbSelectedColor = MaterialTheme.colorScheme.primary,
            selectionActionable = ScrollbarSelectionActionable.WhenVisible,
        ) {
            LazyColumn(
                state = lazyColumnState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                item {
                    AlbumHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        album = albumData,
                        dominantColor = dominantColor,
                        artistsString = artistsString
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                RoundedTag(
                                    text = albumData.albumType.name,
                                )
                            }
                            item {
                                RoundedTag(
                                    text = albumData.totalTracks.toString() + " " + stringResource(
                                        id = R.string.tracks
                                    ),
                                )
                            }
                            item {
                                RoundedTag(
                                    text = stringResource(id = R.string.popularity) + ": " + albumData.popularity.toString(),
                                )
                            }
                            item {
                                RoundedTag(
                                    text = albumData.releaseDate.toCompleteString(),
                                )
                            }
                            item {
                                RoundedTag(
                                    text = albumData.label,
                                )
                            }
                        }
                    }
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        )
                    )
                }
                items(
                    count = albumTracks.itemCount,
                    key = albumTracks.itemKey { item -> item.id },
                    contentType = albumTracks.itemContentType { "album_tracks" }
                ) {
                    val track = albumTracks[it] ?: return@items
                    val trackArtists =
                        track.artists.joinToString(", ") { artist -> artist.name ?: "" }

                    MetadataEntityItem(
                        contentModifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                        songName = track.name,
                        artists = trackArtists,
                        listIndex = it,
                        isExplicit = track.explicit,
                        duration = track.durationMs,
                        onClick = {}
                    ) {
                        viewModel.selectTrackForSheet(track)
                        showTrackSheet = true
                    }
                }
                if (albumData.totalTracks != 1) {
                    loadStateContent(albumTracks) {
                        HorizontalSongCardShimmer(showSongImage = false)
                    }
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        )
                    )
                }
                item {
                    Text(
                        text = stringResource(id = R.string.copyright),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                items(albumData.copyrights) { copyright ->
                    Text(
                        text = "(${copyright.type.identifier})" + " " + copyright.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
        if (showTrackSheet && viewState.trackForSheet != null) {
            TrackBottomSheet(
                simpleTrack = viewState.trackForSheet,
                artworkForSimpleTrack = albumData.images.firstOrNull()?.url ?: SPOTIFY_LOGO_URL,
            ) {
                showTrackSheet = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumHeader(
    modifier: Modifier,
    album: Album,
    dominantColor: Color,
    artistsString: String
) {

    var showArtwork by remember {
        mutableStateOf(true)
    }

    val config = LocalConfiguration.current
    val imageSize =
        (config.screenHeightDp / 3.25) //calculate the image size based on the screen size and the aspect ratio as 1:1 (square) based on the height

    val albumArtwork = album.images.firstOrNull()?.url ?: ""

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .size(imageSize.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 18.dp
                )
            ) {
                if (showArtwork) {
                    AsyncImageImpl(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(
                                1f, matchHeightConstraintsFirst = true
                            )
                            .clip(MaterialTheme.shapes.small),
                        model = albumArtwork,
                        contentDescription = stringResource(id = R.string.album_artwork),
                        onState = { state ->
                            //if it was successful, don't show the placeholder, else show it
                            showArtwork =
                                state !is AsyncImagePainter.State.Error && state !is AsyncImagePainter.State.Empty
                        },
                        contentScale = ContentScale.FillBounds,
                    )
                } else {
                    PlaceholderCreator(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.small),
                        icon = Icons.Default.Album,
                        colorful = false
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarqueeText(
                    text = album.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    maxLines = 1,
                    color = dominantColor,
                    sideGradientColor = MaterialTheme.colorScheme.surface
                )
                Text(
                    text = artistsString,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (album.genres.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LargeCategoryTitle(text = stringResource(id = R.string.genres))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(album.genres) { genre ->
                            RoundedTag(
                                text = genre,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                //create two buttons with a padding between them that fill the max width
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
        }
    }
}
