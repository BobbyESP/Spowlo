package com.bobbyesp.spowlo.ui.pages.history

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.LayersClear
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.database.DownloadedSongInfo
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.components.AudioFilterChip
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.history.HistoryMediaItem
import com.bobbyesp.spowlo.utils.DatabaseUtil
import com.bobbyesp.spowlo.utils.FilesUtil
import com.bobbyesp.spowlo.utils.FilesUtil.getFileSize
import com.bobbyesp.spowlo.utils.GeneralTextUtils

const val AUDIO_REGEX = "(mp3|aac|opus|m4a)$"
const val THUMBNAIL_REGEX = "\\.(jpg|png)$"

fun DownloadedSongInfo.filterByExtractor(author: String?): Boolean {
    return author.isNullOrEmpty() || (this.songAuthor == author)
}

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DownloadsHistoryPage(
    downloadsHistoryViewModel: DownloadsHistoryViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val viewState = downloadsHistoryViewModel.stateFlow.collectAsStateWithLifecycle().value
    val songsListFlow = downloadsHistoryViewModel.songsListFlow

    val songsList = songsListFlow.collectAsState(ArrayList()).value

    LaunchedEffect(key1 = true){
        Log.d("DownloadsHistoryPage", songsList.toString())
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val scope = rememberCoroutineScope()

    val fileSizeMap = remember(songsList.size) {
        mutableMapOf<Int, Long>().apply {
            putAll(songsList.map { Pair(it.id, it.songPath.getFileSize()) })
        }
    }

    var isSelectEnabled by remember { mutableStateOf(false) }
    var showRemoveMultipleItemsDialog by remember { mutableStateOf(false) }

    val filterSet = downloadsHistoryViewModel.filterSetFlow.collectAsState(mutableSetOf()).value
    fun DownloadedSongInfo.filterSort(viewState: DownloadsHistoryViewModel.SongsListViewState): Boolean {
        return filterByExtractor(
            filterSet.elementAtOrNull(viewState.activeFilterIndex)
        )
    }

    @Composable
    fun FilterChipsRow(modifier: Modifier = Modifier) {
        Row(
            modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
                .selectableGroup()
        ) {
            if (filterSet.size > 1) {
                Row {
                    Divider(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .height(24.dp)
                            .width(1f.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    for (i in 0 until filterSet.size) {
                        AudioFilterChip(
                            selected = viewState.activeFilterIndex == i,
                            onClick = { downloadsHistoryViewModel.clickAuthorFilter(i) },
                            label = filterSet.elementAt(i)
                        )
                    }
                }
            }
        }
    }

    val selectedItemIds =
        remember(songsList, isSelectEnabled, viewState) { mutableStateListOf<Int>() }

    val selectedFiles = remember(selectedItemIds.size) {
        mutableStateOf(
            songsList.count { info ->
                selectedItemIds.contains(info.id)
            }
        )
    }

    val selectedFileSizeSum by remember(selectedItemIds.size) {
        derivedStateOf {
            selectedItemIds.fold(0L) { acc: Long, id: Int ->
                acc + fileSizeMap.getOrElse(id) { 0L }
            }
        }
    }

    val visibleItemCount = remember(
        songsList, viewState
    ) { mutableStateOf(songsList.count { it.filterSort(viewState) }) }

    val checkBoxState by remember(selectedItemIds, visibleItemCount) {
        derivedStateOf {
            if (selectedItemIds.isEmpty())
                ToggleableState.Off
            else if (selectedItemIds.size == visibleItemCount.value && selectedItemIds.isNotEmpty())
                ToggleableState.On
            else
                ToggleableState.Indeterminate
        }
    }

    BackHandler(isSelectEnabled) {
        isSelectEnabled = false
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.downloads_history)
                    )
                },
                navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, actions = {
                             Row(){
                                 IconToggleButton(
                                     modifier = Modifier,
                                     onCheckedChange = { isSelectEnabled = !isSelectEnabled },
                                     checked = isSelectEnabled
                                 ) {
                                     Icon(
                                         Icons.Outlined.Checklist,
                                         contentDescription = stringResource(R.string.multiselect_mode)
                                     )
                                 }
                                 IconButton(
                                     modifier = Modifier,
                                     onClick = {

                                     }
                                 ) {
                                     Icon(
                                         Icons.Outlined.LayersClear,
                                         contentDescription = stringResource(R.string.clear_database)
                                     )
                                 }
                             }
                }, scrollBehavior = scrollBehavior
            )
        }, bottomBar = {
            AnimatedVisibility(
                isSelectEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                BottomAppBar(
                    modifier = Modifier
                ) {
                    val selectAllText = stringResource(R.string.select_all)
                    TriStateCheckbox(
                        modifier = Modifier.semantics {
                            this.contentDescription = selectAllText
                        },
                        state = checkBoxState,
                        onClick = {
                            when (checkBoxState) {
                                ToggleableState.On -> selectedItemIds.clear()
                                else -> {
                                    for (item in songsList) {
                                        if (!selectedItemIds.contains(item.id)
                                            && item.filterSort(viewState)
                                        ) {
                                            selectedItemIds.add(item.id)
                                        }
                                    }
                                }
                            }
                        },
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.multiselect_item_count).format(
                            selectedFiles.value,
                        ),
                        style = MaterialTheme.typography.labelLarge
                    )
                    IconButton(
                        onClick = { showRemoveMultipleItemsDialog = true },
                        enabled = selectedItemIds.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = stringResource(id = R.string.remove)
                        )
                    }
                }
            }
        }
    ) { innerPaddings ->
        val cellCount = when (LocalWindowWidthState.current) {
            WindowWidthSizeClass.Expanded -> 2
            else -> 1
        }
        val span: (LazyGridItemSpanScope) -> GridItemSpan = { GridItemSpan(cellCount) }
        LazyVerticalGrid(
            modifier = Modifier
                .padding(innerPaddings), columns = GridCells.Fixed(cellCount)
        ) {
            item(span = span) {
                FilterChipsRow(Modifier.fillMaxWidth())
            }

            for (song in songsList) {
                item(
                    key = song.id,
                    contentType = { song.songPath.contains(AUDIO_REGEX) }){
                    with(song) {
                        AnimatedVisibility(
                            visible = song.filterSort(viewState),
                            exit = shrinkVertically() + fadeOut(),
                            enter = expandVertically() + fadeIn()
                        ) {
                            HistoryMediaItem(
                                modifier = Modifier,
                                songName = songName,
                                author = songAuthor,
                                artworkUrl = thumbnailUrl,
                                songPath = songPath,
                                fileType = songPath.substringAfterLast('.').uppercase(),
                                songFileSize = fileSizeMap[song.id] ?: 0L,
                                songDuration = GeneralTextUtils.convertDuration(songDuration),
                                songSpotifyUrl = songUrl,
                                isSelectEnabled = { isSelectEnabled },
                                isSelected = { selectedItemIds.contains(id) },
                                onSelect = {
                                    if (selectedItemIds.contains(id)) selectedItemIds.remove(id)
                                    else selectedItemIds.add(id)
                                },
                                onClick = { FilesUtil.openFile(songPath) }
                            ) { downloadsHistoryViewModel.showDrawer(scope, song) }
                        }
                    }
                }
            }
        }
    }


}