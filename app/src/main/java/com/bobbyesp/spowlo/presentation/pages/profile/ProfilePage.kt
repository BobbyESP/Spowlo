@file:OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationSpecApi::class
)

package com.bobbyesp.spowlo.presentation.pages.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.palette.graphics.Palette
import coil.ImageLoader
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SpotifyUserInformation
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ext.formatArtistsName
import com.bobbyesp.spowlo.presentation.components.card.CompactArtistCard
import com.bobbyesp.spowlo.presentation.components.card.CompactCardSize
import com.bobbyesp.spowlo.presentation.components.card.CompactSongCard
import com.bobbyesp.spowlo.presentation.components.image.AsyncImage
import com.bobbyesp.spowlo.presentation.components.others.PlayingIndicator
import com.bobbyesp.ui.common.pages.ErrorPage
import com.bobbyesp.ui.common.pages.LoadingPage
import com.bobbyesp.ui.components.button.FilledTonalButtonWithIcon
import com.bobbyesp.ui.components.tags.RoundedTag
import com.bobbyesp.ui.components.text.LargeCategoryTitle
import com.bobbyesp.ui.motion.MotionConstants.DURATION_ENTER
import com.bobbyesp.utilities.states.NoDataScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfilePage(
    viewModel: SpProfilePageViewModel
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val broadcastsState = viewModel.broadcastsViewState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
//            when(event) {
//                is WordInfoViewModel.UIEvent.ShowSnackbar -> {
//                    scaffoldState.snackbarHostState.showSnackbar(
//                        message = event.message
//                    )
//                }
//            }
        }
    }

    LaunchedEffect(broadcastsState.value.metadataState) {
        viewModel.handleBroadcastTrackUpdate()
    }

    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = viewState.value.state,
        label = "Main crossfade Profile Page"
    ) { state ->
        when (state) {
            is NoDataScreenState.Error -> ErrorPage(
                modifier = Modifier.fillMaxSize(),
                throwable = state.throwable,
            ) {
                viewModel.reloadProfileInformation()
            }

            NoDataScreenState.Loading -> LoadingPage()
            NoDataScreenState.Success -> ProfilePageImpl(
                userInfo = viewState.value.profileInformation
                    ?: throw IllegalStateException("Profile information is null"),
                mostPlayedArtistsFlow = viewState.value.userMusicalData.mostPlayedArtists,
                mostPlayedSongsFlow = viewState.value.userMusicalData.mostPlayedSongs,
                broadcastsState = broadcastsState
            )
        }
    }
}

@Composable
private fun ProfilePageImpl(
    userInfo: SpotifyUserInformation,
    mostPlayedArtistsFlow: Flow<PagingData<Artist>> = emptyFlow(),
    mostPlayedSongsFlow: Flow<PagingData<Track>> = emptyFlow(),
    broadcastsState: State<SpProfilePageViewModel.BroadcastsViewState>,
) {
    val mostPlayedArtists = mostPlayedArtistsFlow.collectAsLazyPagingItems()
    val mostPlayedSongs = mostPlayedSongsFlow.collectAsLazyPagingItems()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = it.calculateBottomPadding())
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileHero(
                modifier = Modifier.safeContentPadding(), userInfo = userInfo
            )
            ProfileActions(
                modifier = Modifier
            )
            broadcastsState.value.nowPlayingTrack?.let { track ->
                LargeCategoryTitle(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    text = stringResource(id = R.string.listening_now)
                )
                ListeningNow(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    track = track,
                    isPlaying = broadcastsState.value.playbackState?.playing ?: false
                )
            }

            LargeCategoryTitle(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                text = stringResource(id = R.string.most_played_artists)
            )
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                this.items(
                    count = mostPlayedArtists.itemCount,
                    key = mostPlayedArtists.itemKey(),
                    contentType = mostPlayedArtists.itemContentType()
                ) {
                    mostPlayedArtists[it]?.let { artist ->
                        CompactArtistCard(
                            pictureUrl = artist.images?.firstOrNull()?.url,
                            name = artist.name ?: stringResource(R.string.unknown),
                            genres = artist.genres.joinToString(", "),
                            size = CompactCardSize.MEDIUM
                        )
                    }
                }
            }

            LargeCategoryTitle(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                text = stringResource(id = R.string.most_played_songs)
            )
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                this.items(
                    count = mostPlayedSongs.itemCount,
                    key = mostPlayedSongs.itemKey(),
                    contentType = mostPlayedSongs.itemContentType()
                ) {
                    mostPlayedSongs[it]?.let { song ->
                        CompactSongCard(
                            artworkUrl = song.album.images?.firstOrNull()?.url,
                            name = song.name,
                            artists = song.artists.formatArtistsName(),
                            listIndex = it + 1,
                            size = CompactCardSize.LARGE
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHero(
    modifier: Modifier = Modifier, userInfo: SpotifyUserInformation
) {
    var dominantColor: Color by remember {
        mutableStateOf(Color.Transparent)
    }

    val animatedColor by animateColorAsState(dominantColor, label = "Background color animation")

    Box(
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    animatedColor, Color.Transparent
                ),
            )
        ),
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            userInfo.images?.firstOrNull()?.url?.let { imageUrl ->
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(128.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    imageModel = imageUrl,
                    imageLoader = ImageLoader.Builder(LocalContext.current).allowHardware(false)
                        .crossfade(true).dispatcher(Dispatchers.IO).build(),
                ) { data ->
                    data.drawable?.toBitmap()?.let { bitmap ->
                        Palette.Builder(bitmap).generate { palette ->
                            dominantColor =
                                palette?.mutedSwatch?.rgb?.let { Color(it) } ?: Color.Transparent
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = userInfo.displayName ?: stringResource(R.string.unknown),
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = userInfo.email ?: stringResource(R.string.unknown),
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                )

                RoundedTag(modifier = Modifier, "@${userInfo.id}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                userInfo.followers.total?.let { followers ->
                    Text(
                        text = stringResource(R.string.followers, followers),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileActions(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(12.dp),
                onClick = { /*TODO*/ },
                icon = Icons.Rounded.LibraryMusic,
                text = stringResource(id = R.string.library)
            )
            FilledTonalButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { /*TODO*/ },
                contentPadding = PaddingValues(12.dp),
                icon = Icons.Rounded.LibraryMusic,
                text = stringResource(id = R.string.unknown)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { /*TODO*/ },
                contentPadding = PaddingValues(12.dp),
                icon = Icons.Rounded.LibraryMusic,
                text = stringResource(id = R.string.library)
            )
            FilledTonalButtonWithIcon(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { /*TODO*/ },
                contentPadding = PaddingValues(12.dp),
                icon = Icons.Rounded.LibraryMusic,
                text = stringResource(id = R.string.unknown)
            )
        }
    }
}

@Composable
private fun ListeningNow(
    modifier: Modifier = Modifier, track: Track, isPlaying: Boolean = false
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = isExpanded, label = "Transition between closed and expanded ListeningNow"
        ) {
            when (it) {
                false -> ClosedListeningNow(modifier = modifier,
                    track = track,
                    isPlaying = isPlaying,
                    onClick = { isExpanded = true })

                true -> ExpandedListeningNow(modifier = modifier,
                    track = track,
                    onClick = { isExpanded = false })
            }
        }
    }
}

context(SharedTransitionScope, AnimatedContentScope)
@Composable
private fun ClosedListeningNow(
    modifier: Modifier = Modifier,
    track: Track,
    isPlaying: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState("listeningNowBounds"),
                animatedVisibilityScope = this@AnimatedContentScope,
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(onClick = onClick, onLongClick = { /*TODO*/ }),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(72.dp)
                    .sharedElement(state = rememberSharedContentState("listeningNowImage"),
                        animatedVisibilityScope = this@AnimatedContentScope,
                        boundsTransform = { initialBounds, targetBounds ->
                            keyframes {
                                durationMillis = DURATION_ENTER
                                initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                                targetBounds at DURATION_ENTER
                            }
                        }),
                imageModel = track.album.images?.firstOrNull()?.url,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = track.artists.formatArtistsName(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            AnimatedVisibility(visible = isPlaying) {
                PlayingIndicator(
                    modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

context(AnimatedContentScope, SharedTransitionScope)
@Composable
private fun ExpandedListeningNow(
    modifier: Modifier = Modifier, track: Track, onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState("listeningNowBounds"),
                animatedVisibilityScope = this@AnimatedContentScope,
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(onClick = onClick, onLongClick = { /*TODO*/ }),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(128.dp)
                    .sharedElement(
                        state = rememberSharedContentState("listeningNowImage"),
                        animatedVisibilityScope = this@AnimatedContentScope
                    ),
                imageModel = track.album.images?.firstOrNull()?.url,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = track.artists.formatArtistsName(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}