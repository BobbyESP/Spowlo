package com.bobbyesp.spowlo.ui.pages.search

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
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
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.others.SearchingResult
import com.bobbyesp.spowlo.ui.components.others.own_shimmer.HorizontalSongCardShimmer
import com.bobbyesp.spowlo.ui.components.searchBar.QueryTextBox
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.ui.ext.secondOrNull
import com.bobbyesp.spowlo.ui.pages.search.SearchViewModel.Companion
import com.bobbyesp.spowlo.utils.ui.pages.IdlePage
import kotlinx.coroutines.launch

@Composable
fun SearchPage(
    viewModel: SearchViewModel,
) {
    val bottomInsetsAsPadding =
        LocalPlayerAwareWindowInsets.current.asPaddingValues()
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value
    val uriHandler = LocalUriHandler.current
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

    val paginatedTracks = viewState.searchedTracks.collectAsLazyPagingItems()
    val paginatedAlbums = viewState.searchedAlbums.collectAsLazyPagingItems()
    val paginatedArtists = viewState.searchedArtists.collectAsLazyPagingItems()
    val paginatedPlaylists = viewState.searchedPlaylists.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = bottomInsetsAsPadding.calculateBottomPadding(),
                start = bottomInsetsAsPadding.calculateStartPadding(
                    LocalLayoutDirection.current
                )
            )
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
                                                val selectedMetadataEntity = MetadataEntity(
                                                    type = SpotifyItemType.TRACKS,
                                                    id = track.id,
                                                )

                                                navController.navigate(
                                                    Route.MetadataEntityViewer.createRoute(
                                                        selectedMetadataEntity
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
                                                val selectedMetadataEntity = MetadataEntity(
                                                    type = SpotifyItemType.ALBUMS,
                                                    id = album.id,
                                                )

                                                navController.navigate(
                                                    Route.MetadataEntityViewer.createRoute(
                                                        selectedMetadataEntity
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
                                                artist.externalUrls.spotify?.let { url ->
                                                    uriHandler.openUri(
                                                        url
                                                    )
                                                }
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
                                                playlist.externalUrls.spotify?.let { url ->
                                                    uriHandler.openUri(
                                                        url
                                                    )
                                                }
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
    paginatedItems: LazyPagingItems<T>,
    itemName: (T) -> String,
    itemArtists: (T) -> String,
    itemArtworkUrl: (T) -> String,
    itemType: SpotifyItemType,
    onItemClick: (T) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = paginatedItems.itemCount.toString() + " " + stringResource(id = R.string.results),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
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
        }
        loadStateContent(paginatedItems) {
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
