package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.MediaStoreFilterType
import com.bobbyesp.spowlo.features.lyrics_downloader.data.local.model.Song
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.alertDialogs.PermissionNotGrantedDialog
import com.bobbyesp.spowlo.ui.components.alertDialogs.toPermissionType
import com.bobbyesp.spowlo.ui.components.buttons.BackButton
import com.bobbyesp.spowlo.ui.components.cards.songs.LocalSongCard
import com.bobbyesp.spowlo.ui.components.chips.SingleChoiceChip
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.lazygrid.rememberForeverLazyGridState
import com.bobbyesp.spowlo.ui.components.others.db.searching.RecentSearch
import com.bobbyesp.spowlo.ui.components.searchBar.ExpandableSearchBar
import com.bobbyesp.spowlo.ui.components.text.CategoryTitle
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.components.topbars.SmallTopAppBar
import com.bobbyesp.spowlo.ui.ext.toList
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.ui.util.permissions.PermissionRequestHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi") //Make the linter shut up kek
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LyricsDownloaderPage(
    viewModel: MediaStorePageViewModel
) {
    val currentApiVersion = Build.VERSION.SDK_INT

    val targetPermission = when {
        currentApiVersion < Build.VERSION_CODES.Q -> READ_EXTERNAL_STORAGE

        currentApiVersion < Build.VERSION_CODES.S -> READ_EXTERNAL_STORAGE

        else -> READ_MEDIA_AUDIO
    }

    val storagePermissionState = rememberPermissionState(permission = targetPermission)
    val navController = LocalNavController.current

    PermissionRequestHandler(
        permissionState = storagePermissionState,
        deniedContent = { shouldShowRationale ->
            PermissionNotGrantedDialog(
                neededPermissions = listOf(targetPermission.toPermissionType()),
                onGrantRequest = {
                    storagePermissionState.launchPermissionRequest()
                },
                onDismissRequest = {
                    navController.popBackStack()
                },
                shouldShowRationale = shouldShowRationale
            )
        },
        content = {
            MediaStorePage(
                viewModel = viewModel,
                navController = navController,
                title = {
                    Text(
                        text = Route.LyricsDownloader.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                subtitle = {
                    MarqueeText(
                        text = stringResource(id = R.string.lyrics_downloader_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        fontWeight = FontWeight.Normal
                    )
                },
                fabs = {
                    FloatingActionButton(modifier = Modifier.imePadding(), onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = "Download all lyrics"
                        )
                    }
                },
                onItemClicked = { song ->
                    val artistsList = song.artist.toList()
                    val mainArtist = artistsList.first()

                    val selectedSongParcel = SelectedSong(
                        name = song.title,
                        mainArtist = mainArtist,
                        localSongPath = song.path,
                    )

                    navController.navigate(
                        Route.SelectedSongLyrics.createRoute(
                            selectedSongParcel
                        )
                    ) //Navigate to lyrics page with the parcelable
                }
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
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
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search for songs"
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
        }, modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        var mediaStoreSongs by rememberSaveable(key = "songsList") {
            mutableStateOf<List<Song>>(emptyList())
        }

        when (state) {
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

                mediaStoreSongs = state.mediaStoreSongs

                if (mediaStoreSongs.isEmpty()) {
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
                            scope.launch {
                                viewModel.loadMediaStoreTracks(
                                    context
                                )
                            }
                        }) {
                            Text(text = stringResource(id = R.string.refresh))
                        }
                    }
                } else Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = wantsToSearch,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            ExpandableSearchBar(
                                query = query,
                                onQueryChange = { query = it },
                                onSearch = { queryToSearch ->
                                    scope.launch {
                                        viewModel.loadMediaStoreWithFilter(
                                            context, queryToSearch
                                        )

                                        viewModel.insertSearch(
                                            queryToSearch
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
                                        CategoryTitle(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                                .padding(top = 8.dp),
                                            text = stringResource(id = R.string.filters)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val selectedFilter = viewState.value.filter
                                            SingleChoiceChip(
                                                selected = selectedFilter == MediaStoreFilterType.TITLE,
                                                onClick = {
                                                    viewModel.updateFilter(MediaStoreFilterType.TITLE)
                                                    //if already selected, deselect
                                                    if (selectedFilter == MediaStoreFilterType.TITLE) {
                                                        viewModel.updateFilter(null)
                                                    }
                                                },
                                                label = stringResource(id = R.string.title)
                                            )
                                            SingleChoiceChip(
                                                selected = selectedFilter == MediaStoreFilterType.ARTIST,
                                                onClick = {
                                                    viewModel.updateFilter(MediaStoreFilterType.ARTIST)
                                                    //if already selected, deselect
                                                    if (selectedFilter == MediaStoreFilterType.ARTIST) {
                                                        viewModel.updateFilter(null)
                                                    }
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
                                        RecentSearch(searchEntity = search, onDeleteClick = {
                                            scope.launch(Dispatchers.IO) {
                                                viewModel.deleteSearchById(search.id)
                                            }
                                        }, onClick = {
                                            scope.launch {
                                                viewModel.loadMediaStoreWithFilter(
                                                    context, search.search, search.filter
                                                )
                                            }
                                            activeFullscreenSearching = false
                                        })
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val lazyGridState = rememberForeverLazyGridState(key = "lazyGrid")

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(125.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize(),
                        state = lazyGridState
                    ) {
                        items(mediaStoreSongs) { song ->
                            LocalSongCard(song = song, modifier = Modifier, onClick = {
                                onItemClicked(song)
                            })
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

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LyricsDownloaderPagePreview() {
    AppLocalSettingsProvider(windowWidthSize = WindowWidthSizeClass.Expanded) {
        SpowloTheme {
//            LyricsDownloaderPageImpl(
//                navController = rememberNavController(), viewModel = LyricsDownloaderPageViewModel(
//                    SearchingDbHelper()
//                )
//            )
        }
    }
}