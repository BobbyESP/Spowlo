package com.bobbyesp.spowlo.presentation.pages.profile

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Microwave
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
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
import com.bobbyesp.spowlo.ext.capitalize
import com.bobbyesp.spowlo.ext.formatArtistsName
import com.bobbyesp.spowlo.presentation.components.card.CompactArtistCard
import com.bobbyesp.spowlo.presentation.components.card.CompactCardSize
import com.bobbyesp.spowlo.presentation.components.card.CompactSongCard
import com.bobbyesp.spowlo.presentation.components.image.AsyncImage
import com.bobbyesp.ui.common.pages.ErrorPage
import com.bobbyesp.ui.common.pages.LoadingPage
import com.bobbyesp.ui.components.button.FilledTonalButtonWithIcon
import com.bobbyesp.ui.components.tags.RoundedTag
import com.bobbyesp.ui.components.text.LargeCategoryTitle
import com.bobbyesp.utilities.states.NoDataScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfilePage(
    viewState: State<SpProfilePageViewModel.PageViewState>,
    broadcastsState: State<SpProfilePageViewModel.BroadcastsViewState>,
    onReloadProfileInfo: () -> Unit,
) {
    val viewStateValue = viewState.value
    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = viewStateValue.state,
        label = "Main crossfade Profile Page"
    ) { state ->
        when (state) {
            is NoDataScreenState.Error -> ErrorPage(
                modifier = Modifier.fillMaxSize(),
                throwable = state.throwable,
                onRetry = { onReloadProfileInfo() }
            )

            NoDataScreenState.Loading -> LoadingPage()
            NoDataScreenState.Success -> ProfilePageImpl(
                userInfo = viewStateValue.profileInformation
                    ?: throw IllegalStateException("Profile information is null"),
                mostPlayedArtistsFlow = viewStateValue.userMusicalData.mostPlayedArtists,
                mostPlayedSongsFlow = viewStateValue.userMusicalData.mostPlayedSongs,
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
//            broadcastsState.value.nowPlayingTrack?.let { track ->
//                LargeCategoryTitle(
//                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
//                    text = stringResource(id = R.string.listening_now)
//                )
//            }

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
                ) { index ->
                    mostPlayedArtists[index]?.let { artist ->
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
                ) { index ->
                    mostPlayedSongs[index]?.let { song ->
                        CompactSongCard(
                            artworkUrl = song.album.images?.firstOrNull()?.url,
                            name = song.name,
                            artists = song.artists.formatArtistsName(),
                            listIndex = index + 1,
                            size = CompactCardSize.LARGE
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)
@Composable
private fun ProfileHero(
    modifier: Modifier = Modifier, userInfo: SpotifyUserInformation
) {
    var dominantColor: Color by remember {
        mutableStateOf(Color.Transparent)
    }

    val animatedColor by animateColorAsState(dominantColor, label = "Background color animation")

    val customImageLoader =
        ImageLoader.Builder(LocalContext.current).allowHardware(false).crossfade(true)
            .dispatcher(Dispatchers.IO).build()
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.lets_start_downloading),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W200
                    )
                    Text(
                        text = userInfo.displayName ?: stringResource(id = R.string.unknown),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                    )
                    RoundedTag(modifier = Modifier, "@${userInfo.id}")
                }

                userInfo.images?.firstOrNull()?.url?.let { imageUrl ->
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(72.dp)
                            .border(0.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        imageModel = imageUrl,
                        imageLoader = customImageLoader
                    ) { data ->
                        data.drawable?.toBitmap()?.let { bitmap ->
                            Palette.Builder(bitmap).generate { palette ->
                                dominantColor = palette?.mutedSwatch?.rgb?.let { Color(it) }
                                    ?: Color.Transparent
                            }
                        }
                    }
                }
            }
            FlowRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RoundedTag(
                    imageVector = Icons.Rounded.Microwave,
                    text = userInfo.product?.capitalize() ?: stringResource(R.string.unknown)
                )
                RoundedTag(
                    imageVector = Icons.Rounded.LocationOn,
                    text = userInfo.country ?: stringResource(R.string.unknown)
                )
                RoundedTag(
                    imageVector = Icons.Rounded.Person, text = stringResource(R.string.followers, userInfo.followers.total ?: -1)
                )
                RoundedTag(
                    imageVector = Icons.Rounded.Email,
                    text = userInfo.email ?: stringResource(R.string.unknown)
                )
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