package com.bobbyesp.spowlo.ui.pages.metadata_entities.playlist

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.CardDefaults
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
import com.adamratzman.spotify.models.Playlist
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification
import com.bobbyesp.spowlo.features.inapp_notifications.domain.model.Notification.Companion.toNotification
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.bottomSheets.track.TrackBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalNotificationsManager
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.buttons.FilledButtonWithIcon
import com.bobbyesp.spowlo.ui.components.buttons.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.components.cards.songs.horizontal.MetadataEntityItem
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.others.ProfilePhoto
import com.bobbyesp.spowlo.ui.components.others.own_shimmer.HorizontalSongCardShimmer
import com.bobbyesp.spowlo.ui.components.others.tags.RoundedTag
import com.bobbyesp.spowlo.ui.components.others.tags.RoundedTagWithIcon
import com.bobbyesp.spowlo.ui.components.text.ExpandableText
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.bobbyesp.spowlo.ui.ext.formatArtistsName
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.utils.ui.pages.ErrorPage
import com.bobbyesp.spowlo.utils.ui.pages.LoadingPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionActionable

@Composable
fun PlaylistPage(
    viewModel: PlaylistPageViewModel,
    playlistId: String
) {
    LaunchedEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
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
        label = "Playlist Page Crossfade"
    ) {
        when (it) {
            is PlaylistPageViewModel.Companion.PlaylistPageState.Error -> {
                ErrorPage(error = it.e) {
                    viewModel.viewModelScope.launch(Dispatchers.IO) {
                        viewModel.loadPlaylist(playlistId)
                    }
                }
            }

            PlaylistPageViewModel.Companion.PlaylistPageState.Loading -> {
                LoadingPage()
            }

            is PlaylistPageViewModel.Companion.PlaylistPageState.Success -> {
                PlaylistPageImplementation(viewModel = viewModel, loadedState = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistPageImplementation(
    viewModel: PlaylistPageViewModel,
    loadedState: PlaylistPageViewModel.Companion.PlaylistPageState.Success
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value

    val navController = LocalNavController.current

    val playlistData = loadedState.playlist
    val playlistTracks = viewState.playlistTracksPaginated.collectAsLazyPagingItems()
    val collaborative = playlistData.collaborative
    val collaborativeString =
        if (collaborative) stringResource(id = R.string.collaborative) else stringResource(id = R.string.not_collaborative)

    var showTrackSheet by remember {
        mutableStateOf(false)
    }

    val notificationsManager = LocalNotificationsManager.current

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
                actions = {

                },
                colors = if (transparentAppBar) {
                    TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                } else {
                    TopAppBarDefaults.topAppBarColors()
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
                    PlaylistHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp), playlist = playlistData
                    ) { playlistDownloadInfo ->
                        val notification = playlistDownloadInfo.toNotification()
                        viewModel.downloadPlaylist(
                            downloadInfo = playlistDownloadInfo,
                            onSuccess = {
                                notificationsManager.showNotification(
                                    Notification(
                                        title = "Download finished",
                                        subtitle = "The playlist has been downloaded successfully. ${playlistDownloadInfo.title} by ${playlistDownloadInfo.artist}",
                                        timestamp = System.currentTimeMillis(),
                                        content = null
                                    )
                                )
                            },
                            onFailure = {
                                notificationsManager.showNotification(
                                    Notification(
                                        title = "Download failed",
                                        subtitle = "The download failed. Please try again.",
                                        timestamp = System.currentTimeMillis(),
                                        content = null
                                    )
                                )
                            }
                        )
                        notificationsManager.showNotification(notification)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                RoundedTag(text = playlistData.type)
                            }
                            item {
                                RoundedTagWithIcon(
                                    text = collaborativeString,
                                    icon = if (collaborative) Icons.Default.Person else Icons.Default.PersonOff
                                )
                            }
                            item {
                                RoundedTag(text = "${playlistData.tracks.total} ${stringResource(id = R.string.tracks)}")
                            }
                            item {
                                RoundedTagWithIcon(
                                    text = "${playlistData.followers.total} ${
                                        stringResource(
                                            id = R.string.followers
                                        )
                                    }", icon = Icons.Default.Person
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
                    count = playlistTracks.itemCount,
                    key = playlistTracks.itemKey(),
                    contentType = playlistTracks.itemContentType()
                ) {
                    val track = playlistTracks[it] ?: return@items
                    val trackArtists =
                        track.artists.formatArtistsName()

                    MetadataEntityItem(
                        contentModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        songName = track.name,
                        artists = trackArtists,
                        isExplicit = track.explicit,
                        duration = track.durationMs,
                        imageUrl = track.album.images?.firstOrNull()?.url ?: "",
                        isPlaylist = true,
                        onClick = {}
                    ) {
                        viewModel.selectTrackForSheet(track)
                        showTrackSheet = true
                    }
                }
                loadStateContent(playlistTracks) {
                    HorizontalSongCardShimmer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        showSongImage = true
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        )
                    )
                }
            }
        }
        if (showTrackSheet && viewState.trackForSheet != null) {
            TrackBottomSheet(
                track = viewState.trackForSheet,
                artworkForSimpleTrack = viewState.trackForSheet.album.images?.firstOrNull()?.url
                    ?: App.SPOTIFY_LOGO_URL,
            ) {
                showTrackSheet = false
            }
        }
    }
}

@Composable
private fun PlaylistHeader(
    modifier: Modifier,
    playlist: Playlist,
    dominantColor: Color = MaterialTheme.colorScheme.primary,
    onDownload: (Downloader.DownloadInfo) -> Unit = {}
) {
    var showArtwork by remember {
        mutableStateOf(true)
    }

    val config = LocalConfiguration.current
    val imageSize =
        (config.screenHeightDp / 3.25) //calculate the image size based on the screen size and the aspect ratio as 1:1 (square) based on the height

    val playlistArtwork = playlist.images?.firstOrNull()?.url ?: ""
    val isPublic = playlist.public ?: true
    val isPublicString =
        if (isPublic) stringResource(id = R.string.public_string) else stringResource(
            id = R.string.private_string
        )

    val playlistUrl = playlist.externalUrls.spotify

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
                        model = playlistArtwork,
                        contentDescription = stringResource(id = R.string.playlist_artwork),
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
                        icon = Icons.Rounded.Album,
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
                    text = playlist.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    maxLines = 1,
                    color = dominantColor,
                    basicGradientColor = MaterialTheme.colorScheme.surface
                )
                Row {
                    if (playlist.owner.images.firstOrNull()?.url != null) {
                        ProfilePhoto(
                            photoUrl = playlist.owner.images.firstOrNull()?.url!!,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = playlist.owner.displayName ?: stringResource(id = R.string.unknown),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    RoundedTagWithIcon(
                        text = isPublicString,
                        icon = if (isPublic) Icons.Default.Public else Icons.Default.PublicOff
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                //create two buttons with a padding between them that fill the max width
                FilledButtonWithIcon(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val downloadInfo = Downloader.DownloadInfo(
                            title = playlist.name,
                            artist = playlist.owner.displayName ?: "Unknown",
                            thumbnailUrl = playlistArtwork,
                            url = playlistUrl ?: throw Exception("No URL found for playlist"),
                            type = SpotifyItemType.TRACKS
                        )
                        onDownload(downloadInfo)
                    },
                    icon = Icons.Default.Download,
                    enabled = playlist.public ?: true,
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
            if (playlist.description != null && playlist.description!!.isNotEmpty()) {
                ExpandableText(
                    text = playlist.description!!,
                    maxLines = 2,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}