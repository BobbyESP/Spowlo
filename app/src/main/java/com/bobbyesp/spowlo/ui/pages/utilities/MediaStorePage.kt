package com.bobbyesp.spowlo.ui.pages.utilities

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.features.lyrics_downloader.domain.model.Song
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.cards.songs.LocalSongCard
import com.bobbyesp.spowlo.ui.components.chips.SingleChoiceChip
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.lazygrid.rememberForeverLazyGridState
import com.bobbyesp.spowlo.ui.components.others.db.searching.RecentSearch
import com.bobbyesp.spowlo.ui.components.searchBar.ExpandableSearchBar
import com.bobbyesp.spowlo.ui.components.text.LargeCategoryTitle
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaStorePage(
    navController: NavController = LocalNavController.current,
    viewModel: MediaStorePageViewModel,
    title: @Composable () -> Unit = {},
    subtitle: @Composable () -> Unit = {},
    fabs: @Composable () -> Unit = {},
    onItemClicked: (Song) -> Unit
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()

    val viewState = viewModel.pageViewState.collectAsStateWithLifecycle()
    val state = viewState.value.state

    var query by rememberSaveable(key = "query") {
        mutableStateOf("")
    }

    var activeFullscreenSearching by remember {
        mutableStateOf(false)
    }

    var wantsToSearch by remember {
        mutableStateOf(false)
    }


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val collapsedFraction by remember { mutableFloatStateOf(scrollBehavior.state.collapsedFraction) }
    val safeCollapsedFraction = maxOf(0.05f, collapsedFraction)
    val contraryCollapsedFraction by remember {
        derivedStateOf { 1.0f - safeCollapsedFraction }
    }

    Scaffold(topBar = {
        SmallTopAppBar(scrollBehavior = scrollBehavior, navigationIcon = {
            BackButton {
                navController.popBackStack()
            }
        }, actions = {
            IconButton(
                onClick = {
                    scope.launch {
                        viewModel.loadMediaStoreTracks(
                            context
                        )
                    }
                }, enabled = state is MediaStorePageState.Loaded
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh MediaStore"
                )
            }
            IconButton(
                onClick = {
                    wantsToSearch = !wantsToSearch
                }, enabled = true
            ) {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "Search for songs"
                )
            }

        }, title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                title()
                subtitle()
            }
        })
    }, floatingActionButton = {
        fabs()
    }, modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Crossfade(
            targetState = state, label = "", modifier = Modifier.fillMaxSize()
        ) {
            when (it) {
                is MediaStorePageState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.width(72.dp)
                        )
                    }
                }

                is MediaStorePageState.Loaded -> {
                    val allSearches = viewModel.allSearchesFlow().collectAsStateWithLifecycle(
                        initialValue = emptyList()
                    ).value

                    if (it.mediaStoreSongs.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_songs_found),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    viewModel.loadMediaStoreTracks(
                                        context
                                    )
                                }
                            }) {
                                Text(text = stringResource(id = R.string.refresh))
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                        ) {
                            val lazyGridState = rememberForeverLazyGridState(key = "lazyGrid")

                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(125.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(8.dp),
                                modifier = Modifier.fillMaxSize(),
                                state = lazyGridState
                            ) {
                                items(count = it.mediaStoreSongs.size,
                                    key = { index -> it.mediaStoreSongs[index].id },
                                    contentType = { index -> it.mediaStoreSongs[index].id.toString() }) { index ->
                                    val song = it.mediaStoreSongs[index]
                                    LocalSongCard(song = song,
                                        modifier = Modifier.animateItemPlacement(),
                                        onClick = {
                                            onItemClicked(song)
                                        })
                                }
                            }
                        }
                        val paddingBasedOnOverlap = PaddingValues(
                            top = (paddingValues.calculateTopPadding() * contraryCollapsedFraction - 16.dp).coerceAtLeast(0.dp),
                            start = paddingValues.calculateStartPadding(layoutDirection),
                            end = paddingValues.calculateEndPadding(layoutDirection),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingBasedOnOverlap),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            AnimatedContent(
                                targetState = wantsToSearch,
                                modifier = Modifier.fillMaxWidth(),
                                label = "Want to search animated searchbar"
                            ) { pretendToSearch ->
                                when (pretendToSearch) {
                                    true -> {
                                        ExpandableSearchBar(query = query,
                                            onQueryChange = { query = it },
                                            onSearch = { queryToSearch ->
                                                val finalQuery = queryToSearch.trim()
                                                viewModel.viewModelScope.launch {
                                                    viewModel.loadMediaStoreWithFilter(
                                                        context, finalQuery
                                                    )

                                                    viewModel.insertSearch(
                                                        finalQuery
                                                    )
                                                }
                                                activeFullscreenSearching = false
                                            },
                                            active = activeFullscreenSearching,
                                            onActiveChange = { activeFullscreenSearching = it },
                                            placeholderText = stringResource(id = R.string.search_for_songs),
                                            leadingIcon = Icons.Default.Search,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                ) {
                                                    LargeCategoryTitle(
                                                        modifier = Modifier.padding(horizontal = 8.dp),
                                                        text = stringResource(id = R.string.filters)
                                                    )
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            2.dp
                                                        ),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val selectedFilter = viewState.value.filter
                                                        SingleChoiceChip(
                                                            selected = selectedFilter == MediaStoreFilterType.TITLE,
                                                            onClick = {
                                                                viewModel.updateFilter(
                                                                    MediaStoreFilterType.TITLE
                                                                )
                                                            },
                                                            label = stringResource(id = R.string.title)
                                                        )
                                                        SingleChoiceChip(
                                                            selected = selectedFilter == MediaStoreFilterType.ARTIST,
                                                            onClick = {
                                                                viewModel.updateFilter(
                                                                    MediaStoreFilterType.ARTIST
                                                                )
                                                            },
                                                            label = stringResource(id = R.string.artist)
                                                        )
                                                    }
                                                }
                                            }
                                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                            LazyColumn(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .animateContentSize(),
                                            ) {
                                                items(allSearches) { search ->
                                                    RecentSearch(searchEntity = search,
                                                        onDeleteClick = {
                                                            scope.launch(Dispatchers.IO) {
                                                                viewModel.deleteSearchById(
                                                                    search.id
                                                                )
                                                            }
                                                        },
                                                        onClick = {
                                                            scope.launch {
                                                                viewModel.loadMediaStoreWithFilter(
                                                                    context,
                                                                    search.search,
                                                                    search.filter
                                                                )
                                                            }
                                                            activeFullscreenSearching = false
                                                        })
                                                }
                                            }
                                        }
                                    }

                                    false -> {

                                    }
                                }
                            }
                        }
                    }
                }

                is MediaStorePageState.Error -> {
                    Text(text = "Error")
                }
            }
        }
    }
}