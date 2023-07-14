package com.bobbyesp.spowlo.ui.pages.profile

import SpotifyHorizontalSongCard
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
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
import com.bobbyesp.spowlo.ui.components.cards.songs.ArtistCard
import com.bobbyesp.spowlo.ui.components.cards.songs.SmallSpotifySongCard
import com.bobbyesp.spowlo.ui.components.images.AsyncImageImpl
import com.bobbyesp.spowlo.ui.components.images.PlaceholderCreator
import com.bobbyesp.spowlo.ui.components.text.CategoryTitle
import com.bobbyesp.spowlo.ui.ext.getId
import com.bobbyesp.spowlo.ui.ext.loadStateContent

@Composable
fun ProfilePage(
    viewModel: ProfilePageViewModel
) {
    val context = LocalContext.current.applicationContext
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    var recompositions by remember { mutableIntStateOf(0) }

    LaunchedEffect(true) {
        viewModel.loadPage(context)
    }

    LaunchedEffect(Unit) {
        //recomposition count
        recompositions++
        Log.d("ProfilePage", "Recompositions: $recompositions")
    }

    Crossfade(
        targetState = viewState.value.state,
        label = "Main Crossfade Profile Page"
    ) { state ->
        when (state) {
            is ProfilePageState.Loading -> {
                LoadingPage()
            }

            is ProfilePageState.Error -> {
                ErrorPage()
            }

            is ProfilePageState.Loaded -> {
                PageImplementation(viewModel)
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageImplementation(
    viewModel: ProfilePageViewModel
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    val pageState = viewState.value
    val mostPlayedArtists = pageState.mostPlayedArtists.collectAsLazyPagingItems()
    val mostListenedSongs = pageState.mostPlayedSongs.collectAsLazyPagingItems()

    LaunchedEffect(pageState.metadataState) {
        val id = pageState.metadataState?.playableUri?.id?.getId()
        if (id != null) {
            viewModel.searchSongById(context, id)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sameSongAsBroadcastVerifier(context)
    }

    val userInfo = viewState.value.userInformation

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        canScroll = { true },
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
        }, topBar = {
            LargeTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
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
                            val imageSize =
                                if (scrollBehavior.state.collapsedFraction in 0.7f..1f) 32.dp else 48.dp
                            Box(
                                modifier = Modifier
                                    .animateContentSize(animationSpec = TweenSpec(300))
                                    .size(imageSize)
                                    .clip(CircleShape)
                            ) {
                                AsyncImageImpl(
                                    modifier = Modifier.fillMaxSize(),
                                    model = userInfo.images.lastIndex.let { userInfo.images[it].url },
                                    contentDescription = "Song cover",
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                PlaceholderCreator(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(end = 16.dp)
                                        .clip(CircleShape),
                                    icon = Icons.Default.Person,
                                    colorful = true
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            if (pageState.actualTrack != null) {
                item {
                    CategoryTitle(text = stringResource(id = R.string.listening_now))
                }
                item {
                    SpotifyHorizontalSongCard(
                        modifier = Modifier,
                        isPlaying = pageState.playbackState?.playing ?: false,
                        track = pageState.actualTrack,
                    ) {
                        pageState.actualTrack.externalUrls.spotify?.let { uriHandler.openUri(it) }
                    }
                }
            }
            item {
                CategoryTitle(text = stringResource(id = R.string.most_played_artists))
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
                            item.externalUrls.spotify?.let { it1 -> uriHandler.openUri(it1) }
                        }
                    }
                }
            }
            item {
                CategoryTitle(text = stringResource(id = R.string.most_played_songs))
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
                            modifier = Modifier.width(100.dp),
                            showSpotifyLogo = false,
                            number = it + 1,
                            onClick = {
                                item.externalUrls.spotify?.let { it1 -> uriHandler.openUri(it1) }
                            }
                        )
                    }
                    loadStateContent(mostListenedSongs)
                    loadStateContent(mostPlayedArtists)
                }
            }
        }
    }
}

@Composable
private fun LoadingPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error")
    }
}