package com.bobbyesp.spowlo.ui.pages.history

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.utils.FilesUtil.getFileSize

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DownloadsHistoryPage(
    downloadsHistoryViewModel: DownloadsHistoryViewModel,
    onBackPressed: () -> Unit
) {
    val viewState = downloadsHistoryViewModel.detailViewState.collectAsStateWithLifecycle().value
    val songsListFlow = downloadsHistoryViewModel.songsListFlow

    val songsList = songsListFlow.collectAsState(ArrayList()).value

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

}