package com.bobbyesp.spowlo.ui.pages.searcher

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.AsyncImageImpl
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.QueryTextBox
import com.bobbyesp.spowlo.ui.components.others.shimmer.cards.HorizontalSongCardShimmer
import com.bobbyesp.spowlo.ui.components.text.AnimatedCounter
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.utils.secondOrNull
import kotlinx.coroutines.launch

@Composable
fun SearcherPage(
    viewModel: SearcherPageViewModel = viewModel(), navController: NavController
) {
    val viewState = viewModel.viewStateFlow.collectAsStateWithLifecycle().value

    val (query, onValueChange) = rememberSaveable(key = "searchQuery_searchPage") {
        mutableStateOf("")
    }

    LaunchedEffect(query) {
        viewModel.updateSearchText(query)
    }

    val onItemClick: (String, String) -> Unit = { type, id ->
        navController.navigate(Route.PLAYLIST_PAGE + "/" + type + "/" + id)
    }

    val types = listOf(
        SearchType(searchType = SpotifySearchType.TRACK, onClick = {
            viewModel.chooseSearchTypeAndSearch(SpotifySearchType.TRACK)
        }),
        SearchType(searchType = SpotifySearchType.ALBUM, onClick = {
            viewModel.chooseSearchTypeAndSearch(SpotifySearchType.ALBUM)
        }),
        SearchType(searchType = SpotifySearchType.PLAYLIST, onClick = {
            viewModel.chooseSearchTypeAndSearch(SpotifySearchType.PLAYLIST)
        }),
        SearchType(searchType = SpotifySearchType.ARTIST, onClick = {
            viewModel.chooseSearchTypeAndSearch(SpotifySearchType.ARTIST)
        }),
    )

    val paginatedTracks = viewState.searchedTracks.collectAsLazyPagingItems()
    val paginatedAlbums = viewState.searchedAlbums.collectAsLazyPagingItems()
    val paginatedPlaylists = viewState.searchedPlaylists.collectAsLazyPagingItems()
    val paginatedArtists = viewState.searchedArtists.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QueryTextBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    query = query,
                    onValueChange = onValueChange,
                    onSearchCallback = {
                        viewModel.viewModelScope.launch {
                            viewModel.search()
                        }
                    })
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(types) {
                    SearchTypeChip(
                        modifier = Modifier,
                        searchType = it.searchType,
                        isActive = viewState.activeSearchType == it.searchType,
                        onClick = it.onClick
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(8.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                with(viewState) {
                    when (this.viewState) {
                        is ViewSearchState.Idle -> {
                            Text(
                                text = stringResource(id = R.string.search),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                        is ViewSearchState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is ViewSearchState.Success -> {
                            Crossfade(
                                modifier = Modifier.fillMaxSize(),
                                targetState = activeSearchType,
                                label = ""
                            ) { searchType ->
                                when (searchType) {
                                    SpotifySearchType.TRACK -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedTracks,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.artists.joinToString(", ") { it.name } },
                                            itemArtworkUrl = { item ->
                                                item.album.images.secondOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifySearchType.TRACK,
                                            onItemClick = { track ->
                                                onItemClick(
                                                    SpotifySearchType.TRACK.asString(), track.id
                                                )
                                            })
                                    }

                                    SpotifySearchType.ALBUM -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedAlbums,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.artists.joinToString(", ") { it.name } },
                                            itemArtworkUrl = { item ->
                                                item.images.secondOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifySearchType.ALBUM,
                                            onItemClick = { album ->
                                                onItemClick(
                                                    SpotifySearchType.ALBUM.asString(), album.id
                                                )
                                            })
                                    }

                                    SpotifySearchType.PLAYLIST -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedPlaylists,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.owner.displayName ?: "" },
                                            itemArtworkUrl = { item ->
                                                item.images.firstOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifySearchType.PLAYLIST,
                                            onItemClick = { playlist ->
                                                onItemClick(
                                                    SpotifySearchType.PLAYLIST.asString(), playlist.id
                                                )
                                            })
                                    }

                                    SpotifySearchType.ARTIST -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedArtists,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.name },
                                            itemArtworkUrl = { item ->
                                                item.images.secondOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifySearchType.ARTIST,
                                            onItemClick = { artist ->
                                                onItemClick(
                                                    SpotifySearchType.ARTIST.asString(), artist.id
                                                )
                                            })
                                    }
                                }
                            }
                        }

                        is ViewSearchState.Error -> {
                            Text(text = "Error")
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> ResultsList(
    modifier: Modifier,
    paginatedItems: LazyPagingItems<T>,
    itemName: (T) -> String,
    itemArtists: (T) -> String,
    itemArtworkUrl: (T) -> String,
    itemType: SpotifySearchType,
    onItemClick: (T) -> Unit
) {
    // Get local density from composable
    val localDensity = LocalDensity.current

    // Create element height in dp state
    var columnHeightDp: Dp by remember {
        mutableStateOf(0.dp)
    }

    val shimmerItemsCount = remember {
        derivedStateOf {
            (columnHeightDp / 60.dp).toInt()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
            }
    ) {
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                Color.Transparent
                            ),
                            startY = 30f,
                        )
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .background(Color.Transparent)
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    AnimatedCounter(
                        count = paginatedItems.itemCount,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        modifier = Modifier,
                        text = " " + stringResource(id = R.string.results),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        items(
            count = paginatedItems.itemCount,
            key = paginatedItems.itemKey(),
            contentType = paginatedItems.itemContentType()
        ) { index ->
            val item = paginatedItems[index] as T
            SearchingResult(
                modifier = Modifier.fillMaxWidth(),
                insideModifier = Modifier.padding(vertical = 4.dp),
                name = itemName(item),
                artists = itemArtists(item),
                artworkUrl = itemArtworkUrl(item),
                onClick = {
                    onItemClick(item)
                },
                type = itemType.asLocalizedString()
            )
        }
        loadStateContent(paginatedItems, itemCount = shimmerItemsCount.value) {
            HorizontalSongCardShimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTypeChip(
    modifier: Modifier, searchType: SpotifySearchType, isActive: Boolean, onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        selected = isActive,
        onClick = onClick,
        label = {
            Text(text = searchType.asLocalizedString())
        },
    )
}

@Composable
fun SearchingResult(
    modifier: Modifier = Modifier,
    artworkUrl: String,
    name: String,
    artists: String,
    type: String = stringResource(id = R.string.track),
    insideModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier.clickable { onClick() }) {
        Row(
            modifier = insideModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically, //This makes all go to the center
        ) {
            AsyncImageImpl(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .size(48.dp)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .clip(MaterialTheme.shapes.extraSmall),
                model = artworkUrl,
                contentDescription = "Song cover",
                contentScale = ContentScale.Crop,
                isPreview = false
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MarqueeText(
                            text = name,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.8f
                            ),
                        )
                    }
                    if (artists.isNotEmpty()) {
                        MarqueeText(
                            text = "$type • $artists",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.8f
                            ),
                        )
                    } else {
                        MarqueeText(
                            text = type,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            basicGradientColor = MaterialTheme.colorScheme.surface.copy(
                                alpha = 0.8f
                            ),
                        )
                    }
                }
            }
        }
    }
}

private data class SearchType(
    val searchType: SpotifySearchType, val onClick: () -> Unit
)