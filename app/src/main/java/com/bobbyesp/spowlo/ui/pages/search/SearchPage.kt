package com.bobbyesp.spowlo.ui.pages.search

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.SpotifyItemType
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.others.SearchingResult
import com.bobbyesp.spowlo.ui.components.others.own_shimmer.HorizontalSongCardShimmer
import com.bobbyesp.spowlo.ui.components.searchBar.QueryTextBox
import com.bobbyesp.spowlo.ui.components.text.AnimatedCounter
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.ui.ext.secondOrNull
import com.bobbyesp.spowlo.ui.pages.search.SearchViewModel.Companion
import com.bobbyesp.spowlo.utils.MetadataEntityUtil.navigateToEntity
import com.bobbyesp.spowlo.utils.ui.pages.IdlePage
import kotlinx.coroutines.launch

@Composable
fun SearchPage(
    viewModel: SearchViewModel,
) {
    val bottomInsetsAsPadding =
        LocalPlayerAwareWindowInsets.current.asPaddingValues()
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value
    val navController = LocalNavController.current

    val (query, onValueChange) = rememberSaveable(key = "searchQuery_searchPage") {
        mutableStateOf("")
    }

    LaunchedEffect(query) {
        viewModel.updateQuery(query)
    }

    val types = listOf(
        SearchType(
            searchType = SpotifyItemType.TRACKS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SpotifyItemType.TRACKS)
            }
        ),
        SearchType(
            searchType = SpotifyItemType.ALBUMS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SpotifyItemType.ALBUMS)
            }
        ),
        SearchType(
            searchType = SpotifyItemType.ARTISTS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SpotifyItemType.ARTISTS)
            }
        ),
        SearchType(
            searchType = SpotifyItemType.PLAYLISTS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SpotifyItemType.PLAYLISTS)
            }
        ),
    )

    val paginatedTracks = viewState.searchedTracks?.collectAsLazyPagingItems()
    val paginatedAlbums = viewState.searchedAlbums?.collectAsLazyPagingItems()
    val paginatedArtists = viewState.searchedArtists?.collectAsLazyPagingItems()
    val paginatedPlaylists = viewState.searchedPlaylists?.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = bottomInsetsAsPadding.calculateBottomPadding(),
                start = bottomInsetsAsPadding.calculateStartPadding(
                    LocalLayoutDirection.current
                )
            ),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            QueryTextBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                query = query,
                onValueChange = onValueChange,
                onSearchCallback = {
                    viewModel.viewModelScope.launch {
                        viewModel.search()
                    }
                }
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                    when (this.searchViewState) {
                        is Companion.SearchViewState.Idle -> {
                            IdlePage()
                        }

                        is Companion.SearchViewState.Loading -> {
                            CircularProgressIndicator()
                        }

                        is Companion.SearchViewState.Success -> {
                            Crossfade(
                                modifier = Modifier.fillMaxSize(),
                                targetState = activeSearchType, label = ""
                            ) { searchType ->
                                when (searchType) {
                                    SpotifyItemType.TRACKS -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedTracks,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.artists.joinToString(", ") { it.name } },
                                            itemArtworkUrl = { item ->
                                                item.album.images.secondOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifyItemType.TRACKS,
                                            onItemClick = { track ->
                                                navigateToEntity(
                                                    navController = navController,
                                                    metadataEntity = MetadataEntity(
                                                        type = SpotifyItemType.TRACKS,
                                                        id = track.id,
                                                    )
                                                )
                                            }
                                        )
                                    }

                                    SpotifyItemType.ALBUMS -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedAlbums,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.artists.joinToString(", ") { it.name } },
                                            itemArtworkUrl = { item ->
                                                item.images.secondOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifyItemType.ALBUMS,
                                            onItemClick = { album ->
                                                navigateToEntity(
                                                    navController = navController,
                                                    metadataEntity = MetadataEntity(
                                                        type = SpotifyItemType.ALBUMS,
                                                        id = album.id,
                                                    )
                                                )
                                            }
                                        )
                                    }

                                    SpotifyItemType.ARTISTS -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedArtists,
                                            itemName = { item -> item.name },
                                            itemArtists = { _ -> "" },
                                            itemArtworkUrl = { item ->
                                                item.images.secondOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifyItemType.ARTISTS,
                                            onItemClick = { artist ->
                                                navigateToEntity(
                                                    navController = navController,
                                                    metadataEntity = MetadataEntity(
                                                        type = SpotifyItemType.ARTISTS,
                                                        id = artist.id,
                                                    )
                                                )
                                            }
                                        )
                                    }

                                    SpotifyItemType.PLAYLISTS -> {
                                        ResultsList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedItems = paginatedPlaylists,
                                            itemName = { item -> item.name },
                                            itemArtists = { item -> item.owner.displayName ?: "" },
                                            itemArtworkUrl = { item ->
                                                item.images.firstOrNull()?.url ?: ""
                                            },
                                            itemType = SpotifyItemType.PLAYLISTS,
                                            onItemClick = { playlist ->
                                                navigateToEntity(
                                                    navController = navController,
                                                    metadataEntity = MetadataEntity(
                                                        type = SpotifyItemType.PLAYLISTS,
                                                        id = playlist.id,
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        is Companion.SearchViewState.Error -> {
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
    paginatedItems: LazyPagingItems<T>?,
    itemName: (T) -> String,
    itemArtists: (T) -> String,
    itemArtworkUrl: (T) -> String,
    itemType: SpotifyItemType,
    onItemClick: (T) -> Unit
) {

    val itemCount = paginatedItems?.itemCount
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
            },
        state = rememberLazyListState(),
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
                        count = paginatedItems?.itemCount ?: 0,
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
        paginatedItems?.let {
            items(
                count = it.itemCount,
                key = paginatedItems.itemKey(),
                contentType = paginatedItems.itemContentType()
            ) { index ->
                if (index in 0 until it.itemCount) {
                    // Inside composable block
                    val item = paginatedItems[index] as T
                    SearchingResult(
                        modifier = Modifier
                            .fillMaxWidth(),
                        insideModifier = Modifier.padding(vertical = 6.dp),
                        name = itemName(item),
                        artists = itemArtists(item),
                        artworkUrl = itemArtworkUrl(item),
                        onClick = {
                            onItemClick(item)
                        },
                        type = itemType.toComposableStringSingular()
                    )
                } else {
                    // Handle the case where the index is out of bounds
                    // You can log a message, throw an exception, or provide a default item
                    Log.e("YourTag", "Index $index is out of bounds for list size $itemCount")
                    // Alternatively, you can provide a default item or UI element
                    // val defaultItem = getDefaultItem()
                    // SearchingResult(/* Use defaultItem here */)
                }
            }
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


@Composable
fun SearchTypeChip(
    modifier: Modifier,
    searchType: SpotifyItemType,
    isActive: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        selected = isActive,
        onClick = onClick,
        label = {
            Text(text = searchType.toComposableStringPlural())
        },
    )
}

private data class SearchType(
    val searchType: SpotifyItemType,
    val onClick: () -> Unit
)
