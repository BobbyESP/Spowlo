package com.bobbyesp.spowlo.ui.pages.search

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.others.SearchingResult
import com.bobbyesp.spowlo.ui.components.searchBar.QueryTextBox
import com.bobbyesp.spowlo.ui.ext.loadStateContent
import com.bobbyesp.spowlo.ui.ext.secondOrNull
import com.bobbyesp.spowlo.ui.pages.others.IdlePage
import kotlinx.coroutines.launch

@Composable
fun SearchPage(
    viewModel: SearchViewModel,
) {
    val bottomInsetsAsPadding =
        LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()
    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    val (query, onValueChange) = remember {
        mutableStateOf("")
    }

    LaunchedEffect(query) {
        viewModel.updateQuery(query)
    }

    val types = listOf(
        SearchType(
            searchType = SearchTypes.TRACKS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SearchTypes.TRACKS, context)
            }
        ),
        SearchType(
            searchType = SearchTypes.ALBUMS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SearchTypes.ALBUMS, context)
            }
        ),
        SearchType(
            searchType = SearchTypes.ARTISTS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SearchTypes.ARTISTS, context)
            }
        ),
        SearchType(
            searchType = SearchTypes.PLAYLISTS,
            onClick = {
                viewModel.chooseSearchTypeAndSearch(SearchTypes.PLAYLISTS, context)
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
            .padding(bottom = bottomInsetsAsPadding)
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
                        viewModel.search(context = context)
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
                    when(this.searchViewState) {
                        is SearchViewState.Idle -> {
                            IdlePage()
                        }
                        is SearchViewState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is SearchViewState.Success -> {
                            Crossfade(
                                modifier = Modifier.fillMaxSize(),
                                targetState = activeSearchType, label = ""
                            ) { searchType ->
                                when(searchType) {
                                    SearchTypes.TRACKS -> {
                                        TracksList(
                                            modifier = Modifier.fillMaxSize(),
                                            paginatedTracks = paginatedTracks,
                                        )
                                    }
                                    SearchTypes.ALBUMS -> {
//                                        AlbumsList(
//                                            modifier = Modifier.fillMaxSize(),
//                                            paginatedAlbums = paginatedAlbums,
//                                            onAlbumClick = {
//                                                viewModel.onAlbumClick(it)
//                                            }
//                                        )
                                    }
                                    SearchTypes.ARTISTS -> {
//                                        ArtistsList(
//                                            modifier = Modifier.fillMaxSize(),
//                                            paginatedArtists = paginatedArtists,
//                                            onArtistClick = {
//                                                viewModel.onArtistClick(it)
//                                            }
//                                        )
                                    }
                                    SearchTypes.PLAYLISTS -> {
//                                        PlaylistsList(
//                                            modifier = Modifier.fillMaxSize(),
//                                            paginatedPlaylists = paginatedPlaylists,
//                                            onPlaylistClick = {
//                                                viewModel.onPlaylistClick(it)
//                                            }
//                                        )
                                    }
                                }
                            }
                        }
                        is SearchViewState.Error -> {
                            Text(text = "Error")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TracksList(
    modifier: Modifier,
    paginatedTracks: LazyPagingItems<Track>
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier
    ) {
        items(
            count = paginatedTracks.itemCount,
            key = paginatedTracks.itemKey(),
            contentType = paginatedTracks.itemContentType()
        ) { index ->
            val item = paginatedTracks[index]
            SearchingResult(
                modifier = Modifier
                    .fillMaxWidth(),
                name = item?.name ?: "",
                artists = item?.artists?.joinToString(", ") { it.name } ?: "",
                artworkUrl = item?.album?.images?.secondOrNull()?.url ?: "",
                onClick = {
                    uriHandler.openUri( item?.externalUrls?.spotify ?: "")
                }
            )
        }
        loadStateContent(paginatedTracks) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun <T : Any> PaginatedSearchList(
    modifier: Modifier = Modifier,
    paginatedItems: LazyPagingItems<T>,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTypeChip(
    modifier: Modifier,
    searchType: SearchTypes,
    isActive: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        selected = isActive,
        onClick = onClick,
        label = {
            Text(text = searchType.toComposableString())
        },
    )
}

private data class SearchType(
    val searchType: SearchTypes,
    val onClick: () -> Unit
)
