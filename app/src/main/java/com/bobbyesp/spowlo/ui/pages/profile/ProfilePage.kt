package com.bobbyesp.spowlo.ui.pages.profile

//noinspection UsingMaterialAndMaterial3Libraries
import SpotifyHorizontalSongCard
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.bottomSheets.track.TrackBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.cards.songs.ArtistCard
import com.bobbyesp.spowlo.ui.components.cards.songs.SmallSpotifySongCard
import com.bobbyesp.spowlo.ui.components.cards.songs.horizontal.RecentlyPlayedSongCard
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.others.own_shimmer.SmallSongCardShimmer
import com.bobbyesp.spowlo.ui.components.text.LargeCategoryTitle
import com.bobbyesp.spowlo.ui.ext.getId
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.ui.ext.navigateToMetadataEntity
import com.bobbyesp.spowlo.ui.ext.playerSafePadding
import com.bobbyesp.spowlo.ui.ext.toInt
import com.bobbyesp.spowlo.utils.ui.pages.ErrorPage
import com.bobbyesp.spowlo.utils.ui.pages.LoadingPage
import com.bobbyesp.spowlo.utils.ui.pages.PageStateWithThrowable
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfilePage(
    viewModel: ProfilePageViewModel
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    Crossfade(
        modifier = Modifier
            .fillMaxSize()
            .playerSafePadding(),
        targetState = viewState.value.state,
        label = "Main crossfade Profile Page"
    ) { state ->
        when (state) {
            is PageStateWithThrowable.Loading -> {
                LoadingPage()
            }

            is PageStateWithThrowable.Error -> {
                ErrorPage(
                    error = state.exception.message ?: stringResource(id = R.string.unknown),
                    onRetry = {
                        viewModel.reloadPage()
                    }
                )
            }

            is PageStateWithThrowable.Success -> {
                PageImplementation(viewModel)
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun PageImplementation(
    viewModel: ProfilePageViewModel
) {
    val navController = LocalNavController.current

    var showSheet by remember { mutableStateOf(false) }

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val pageState = viewState.value

    val mostPlayedArtists = pageState.mostPlayedArtists.collectAsLazyPagingItems()
    val mostListenedSongs = pageState.mostPlayedSongs.collectAsLazyPagingItems()
    val recentlyPlayedSongs = pageState.recentlyPlayedSongs
    val refreshing = pageState.isRefreshing

    val pullRefreshState = rememberPullRefreshState(refreshing, {
        viewModel.viewModelScope.launch {
            viewModel.reloadPage()
        }
    })

    LaunchedEffect(pageState.metadataState) {
        val id = pageState.metadataState?.playableUri?.id?.getId()
        if (id != null) {
            viewModel.searchSongByIdAndUpdateUi(id)
        }
        viewModel.sameSongAsBroadcastVerifier()
    }

    val userInfo = viewState.value.userInformation

    val dateRanges = listOf(
        stringResource(id = R.string.four_weeks),
        stringResource(id = R.string.six_months),
        stringResource(id = R.string.all_time)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
        }
    )
    { paddingValues ->
        Box(Modifier.pullRefresh(pullRefreshState)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 10.dp),
                userScrollEnabled = true,
            ) {
                stickyHeader(key = "header_profilePage") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        Color.Transparent
                                    ),
                                    endY = 175f
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = userInfo?.displayName
                                    ?: stringResource(id = R.string.unknown),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (userInfo?.id != null) "@${userInfo.id}" else stringResource(
                                    id = R.string.unknown
                                ),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.6f
                                    )
                                ),
                            )
                        }
                        if (userInfo?.images?.isNotEmpty() == true) {
                            val imageSize = 48.dp
                            Box(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .size(imageSize)
                                    .clip(CircleShape)
                            ) {
                                AsyncImageImpl(
                                    modifier = Modifier.fillMaxSize(),
                                    model = userInfo.images.lastIndex.let { userInfo.images[it].url },
                                    contentDescription = "Song cover",
                                    contentScale = ContentScale.FillBounds,
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                PlaceholderCreator(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.Center),
                                    icon = Icons.Default.Person,
                                    colorful = true
                                )
                            }
                        }
                    }
                }
                item {
                    LargeCategoryTitle(text = stringResource(id = R.string.data_time_range))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SingleChoiceSegmentedButtonRow {
                            dateRanges.forEachIndexed { index, date ->
                                SegmentedButton(
                                    selected = viewState.value.actualTimeRange.toInt() == index,
                                    onClick = {
                                        viewModel.updateTimeRangeAndReload(index)
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = dateRanges.size
                                    ),
                                ) {
                                    Text(text = date)
                                }
                            }
                        }
                    }
                }
                if (pageState.actualTrack != null) {
                    item {
                        LargeCategoryTitle(text = stringResource(id = R.string.listening_now))
                    }
                    item {
                        SpotifyHorizontalSongCard(
                            modifier = Modifier,
                            isPlaying = pageState.playbackState?.playing ?: false,
                            track = pageState.actualTrack,
                            onLongClick = {
                                viewModel.selectTrackForSheet(pageState.actualTrack)
                                showSheet = true
                            }
                        ) {
                            val selectedMetadataEntity = MetadataEntity(
                                type = SpotifyItemType.TRACKS,
                                id = pageState.actualTrack.id,
                            )

                            navController.navigateToMetadataEntity(selectedMetadataEntity)
                        }
                    }
                }
                item {
                    LargeCategoryTitle(text = stringResource(id = R.string.most_played_artists))
                }
                item {
                    LazyRow(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        this.items(
                            count = mostPlayedArtists.itemCount,
                            key = mostPlayedArtists.itemKey(),
                            contentType = mostPlayedArtists.itemContentType()
                        ) {
                            val item = mostPlayedArtists[it]

                            ArtistCard(
                                artist = item!!, modifier = Modifier
                                    .height(120.dp)
                                    .width(80.dp)
                            ) {
                                val selectedMetadataEntity = MetadataEntity(
                                    type = SpotifyItemType.ARTISTS,
                                    id = item.id,
                                )

                                navController.navigateToMetadataEntity(selectedMetadataEntity)

                            }
                        }
                    }
                }
                item {
                    LargeCategoryTitle(text = stringResource(id = R.string.most_played_songs))
                }
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        state = rememberLazyListState(),
                    ) {
                        items(
                            count = mostListenedSongs.itemCount,
                            key = mostListenedSongs.itemKey(),
                            contentType = mostListenedSongs.itemContentType()
                        ) {
                            val item = mostListenedSongs[it]
                            SmallSpotifySongCard(
                                track = item!!,
                                modifier = Modifier,
                                size = 110.dp,
                                showSpotifyLogo = false,
                                number = it + 1,
                                onClick = {
                                    val selectedMetadataEntity = MetadataEntity(
                                        type = SpotifyItemType.TRACKS,
                                        id = item.id,
                                    )

                                    navController.navigateToMetadataEntity(selectedMetadataEntity)

                                },
                                onLongClick = {
                                    viewModel.selectTrackForSheet(item)
                                    showSheet = true
                                }
                            )
                        }
                        loadStateContent(mostListenedSongs, itemCount = 7) {
                            SmallSongCardShimmer()
                        }
                        loadStateContent(mostPlayedArtists, itemCount = 7) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
                item {
                    LargeCategoryTitle(text = stringResource(id = R.string.recently_played))
                }
                items(
                    count = recentlyPlayedSongs.size,
                    key = { index -> recentlyPlayedSongs[index].track.id + index.toString() },
                ) { index ->
                    val item = recentlyPlayedSongs[index]
                    RecentlyPlayedSongCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraSmall),
                        playHistoryItem = item,
                        onClick = {
                            val selectedMetadataEntity = MetadataEntity(
                                type = SpotifyItemType.TRACKS,
                                id = item.track.id,
                            )

                            navController.navigateToMetadataEntity(selectedMetadataEntity)

                        },
                        onLongClick = {
                            viewModel.selectTrackForSheet(item.track)
                            showSheet = true
                        }
                    )
                    if (item != recentlyPlayedSongs.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            PullRefreshIndicator(
                refreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (showSheet) {
            TrackBottomSheet(track = viewState.value.selectedTrackForSheet!!) {
                showSheet = false
            }
        }
    }
}
