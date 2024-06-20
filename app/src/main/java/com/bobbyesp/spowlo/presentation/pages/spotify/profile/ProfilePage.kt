package com.bobbyesp.spowlo.presentation.pages.spotify.profile

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bobbyesp.spowlo.presentation.components.spotify.card.CompactArtistCard
import com.bobbyesp.spowlo.presentation.components.spotify.card.CompactCardSize
import com.bobbyesp.spowlo.presentation.components.spotify.card.CompactSongCard
import com.bobbyesp.spowlo.presentation.components.spotify.image.AsyncImage
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
    viewModel: SpProfilePageViewModel
) {
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()

    Crossfade(
        modifier = Modifier
            .fillMaxSize(),
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
                mostPlayedSongsFlow = viewState.value.userMusicalData.mostPlayedSongs
            )
        }
    }
}

@Composable
private fun ProfilePageImpl(
    userInfo: SpotifyUserInformation,
    mostPlayedArtistsFlow: Flow<PagingData<Artist>> = emptyFlow(),
    mostPlayedSongsFlow: Flow<PagingData<Track>> = emptyFlow()
) {
    val mostPlayedArtists = mostPlayedArtistsFlow.collectAsLazyPagingItems()
    val mostPlayedSongs = mostPlayedSongsFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        userScrollEnabled = true,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ProfileHero(
                modifier = Modifier.safeContentPadding(),
                userInfo = userInfo
            )
        }
        item {
            Column(
                modifier = Modifier
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
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(12.dp),
                        onClick = { /*TODO*/ },
                        icon = Icons.Rounded.LibraryMusic,
                        text = stringResource(id = R.string.library)
                    )
                    FilledTonalButtonWithIcon(
                        modifier = Modifier.fillMaxWidth().weight(1f),
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
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = MaterialTheme.shapes.small,
                        onClick = { /*TODO*/ },
                        contentPadding = PaddingValues(12.dp),
                        icon = Icons.Rounded.LibraryMusic,
                        text = stringResource(id = R.string.library)
                    )
                    FilledTonalButtonWithIcon(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = MaterialTheme.shapes.small,
                        onClick = { /*TODO*/ },
                        contentPadding = PaddingValues(12.dp),
                        icon = Icons.Rounded.LibraryMusic,
                        text = stringResource(id = R.string.unknown)
                    )
                }
            }
        }
        item {
            LargeCategoryTitle(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                text = stringResource(id = R.string.most_played_artists)
            )
        }
        item {
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
        }

        item {
            LargeCategoryTitle(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                text = stringResource(id = R.string.most_played_songs)
            )
        }

        item {
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
    modifier: Modifier = Modifier,
    userInfo: SpotifyUserInformation
) {
    var dominantColor: Color by remember {
        mutableStateOf(Color.Transparent)
    }

    val animatedColor by animateColorAsState(dominantColor, label = "Background color animation")

    Box(
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    animatedColor,
                    Color.Transparent
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
                    imageLoader = ImageLoader.Builder(LocalContext.current)
                        .allowHardware(false)
                        .crossfade(true)
                        .dispatcher(Dispatchers.IO)
                        .build(),
                ) { data ->
                    data.drawable?.toBitmap()?.let { bitmap ->
                        Palette.Builder(bitmap).generate { palette ->
                            dominantColor = palette?.mutedSwatch?.rgb?.let { Color(it) }
                                ?: Color.Transparent
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