package com.bobbyesp.spowlo.ui.pages.downloader.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.features.downloader.domain.DownloadTask.DownloadState.Companion.toTaskState
import com.bobbyesp.spowlo.ui.components.dividers.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.others.download_tasks.DownloadingTaskItem
import com.bobbyesp.spowlo.ui.ext.playerSafePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderTasksPage(
    onNavigateToDetail: (Int) -> Unit? = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .playerSafePadding(),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.download_tasks),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }, actions = {}, scrollBehavior = scrollBehavior
            )
        }) { paddings ->
        val clipboardManager = LocalClipboardManager.current
        LazyColumn(
            modifier = Modifier.padding(paddings),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(Downloader.mutableTaskList.values.toList()) { download ->
                download.run {
                    DownloadingTaskItem(taskItem = download, status = state.toTaskState()) {
                        onNavigateToDetail(hashCode())
                    }
//                    DownloadingTaskItem(status = state.toStatus(),
//                        progress = if (state is Downloader.DownloadTask.State.Running) state.progress else 0f,
//                        progressText = currentLine,
//                        url = url,
//                        header = it.taskName,
//                        onCopyError = {
//                            onCopyError(clipboardManager)
//                        },
//                        onCancel = {
//                            onCancel()
//                        },
//                        onRestart = {
//                            onRestart()
//                        },
//                        onCopyLog = {
//                            onCopyLog(clipboardManager)
//                        },
//                        onShowLog = {
//                            onNavigateToDetail(hashCode())
//                        },
//                        onCopyLink = {
//                            onCopyUrl(clipboardManager)
//                        })
                }
            }
        }
        if (Downloader.mutableTaskList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_running_downloads),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(
                                vertical = 24.dp
                            )
                            .fillMaxWidth(0.75f)
                    )
                    Text(
                        text = stringResource(R.string.no_running_downloads_description),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}