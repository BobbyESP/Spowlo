package com.bobbyesp.spowlo.ui.pages.downloader.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.downloader.Downloader
import com.bobbyesp.spowlo.features.downloader.domain.DownloadTask
import com.bobbyesp.spowlo.ui.components.chips.ButtonChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenConsoleOutput(
    onBackPressed: () -> Unit,
    taskHashCode: Int
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val task = Downloader.mutableTaskList.values.find { it.hashCode() == taskHashCode } ?: return
    val clipboardManager = LocalClipboardManager.current

    val minFontSize = 8
    val maxFontSize = 32

    var mutableFontSize by remember { mutableIntStateOf(14) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.download_log),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                )
            }, navigationIcon = {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(Icons.Rounded.Close, stringResource(R.string.close))
                }
            }, actions = {
            }, scrollBehavior = scrollBehavior
            )
        }, bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    task.run {
                        ButtonChip(
                            icon = Icons.Rounded.ContentCopy,
                            label = stringResource(id = R.string.copy_output)
                        ) {
                            onCopyOutput(context, clipboardManager)
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    color = Color.Transparent,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(id = R.string.font_size),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = mutableFontSize.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace
                            )
                            ButtonChip(
                                label = "-",
                                onClick = {
                                    mutableFontSize =
                                        (mutableFontSize - 2).coerceIn(minFontSize, maxFontSize)
                                }

                            )
                            ButtonChip(
                                label = "+",
                                onClick = {
                                    mutableFontSize =
                                        (mutableFontSize + 2).coerceIn(minFontSize, maxFontSize)
                                }
                            )
                        }
                        if (state is DownloadTask.DownloadState.Failed)
                            ButtonChip(
                                icon = Icons.Rounded.Error,
                                label = stringResource(id = R.string.copy_error_report),
                                iconColor = MaterialTheme.colorScheme.error,
                            ) {
                                onCopyError(context, clipboardManager)
                            }
                        if (state is DownloadTask.DownloadState.Cancelled)
                            ButtonChip(
                                icon = Icons.Rounded.RestartAlt,
                                label = stringResource(id = R.string.restart_task),
                            ) {
                                onRestart()
                            }
                    }
                }
            }
        }) { paddings ->
        val scrollState = rememberScrollState()
        LaunchedEffect(scrollState.maxValue) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
        Column(
            modifier = Modifier
                .padding(paddings)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .horizontalScroll(rememberScrollState())
        ) {
            SelectionContainer {
                Text(
                    modifier = Modifier.widthIn(max = 800.dp),
                    text = task.output,
                    fontSize = mutableFontSize.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                )
            }
        }
    }
}