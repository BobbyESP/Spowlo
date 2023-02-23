package com.bobbyesp.spowlo.ui.dialogs

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.components.BottomDrawer
import com.bobbyesp.spowlo.ui.components.FilledTonalButtonWithIcon
import com.bobbyesp.spowlo.ui.components.HorizontalDivider
import com.bobbyesp.spowlo.ui.components.OutlinedButtonWithIcon
import com.bobbyesp.spowlo.ui.pages.imageLoader
import com.bobbyesp.spowlo.ui.theme.contraryColor
import com.bobbyesp.spowlo.utils.ChromeCustomTabsUtil
import com.bobbyesp.spowlo.utils.GeneralTextUtils
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.UpdateUtil
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun UpdaterBottomDrawer(
    latestRelease: UpdateUtil.LatestRelease,
) {
    val drawerViewState = UpdateUtil.updateViewState.collectAsStateWithLifecycle().value
    var currentDownloadStatus by remember { mutableStateOf(UpdateUtil.DownloadStatus.NotYet as UpdateUtil.DownloadStatus) }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    UpdaterBottomDrawerImpl(
        drawerState = drawerViewState.drawerState,
        version = latestRelease.name.toString(),
        changelog = latestRelease.body.toString(),
        downloadUrl = latestRelease.htmlUrl.toString(),
        downloadStatus = currentDownloadStatus,
        onConfirmUpdate = {
            scope.launch(Dispatchers.IO) {
                runCatching {
                    UpdateUtil.downloadApk(latestRelease = latestRelease)
                        .collect { downloadStatus ->
                            currentDownloadStatus = downloadStatus
                            if (downloadStatus is UpdateUtil.DownloadStatus.Finished) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    UpdateUtil.installLatestApk()
                                }
                            }
                        }
                }.onFailure {
                    it.printStackTrace()
                    currentDownloadStatus = UpdateUtil.DownloadStatus.NotYet
                    ToastUtil.makeToastSuspend(context.getString(R.string.app_update_failed))
                    return@launch
                }
            }
        },
        onDismiss = {
            hideDrawer(scope)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UpdaterBottomDrawerImpl(
    drawerState: ModalBottomSheetState,
    version: String,
    changelog: String,
    downloadUrl: String,
    downloadStatus: UpdateUtil.DownloadStatus,
    onConfirmUpdate: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current


    BottomDrawer(drawerState = drawerState, sheetContent = {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = version,
                        style = MaterialTheme.typography.headlineMedium,
                        color = contraryColor()
                    )
                    Text(
                        text = stringResource(id = R.string.update_available),
                        style = MaterialTheme.typography.labelMedium,
                        color = contraryColor(),
                        modifier = Modifier.alpha(0.6f)
                    )
                }
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(downloadUrl))
                    ToastUtil.makeToast(context.getString(R.string.link_copied))
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp))
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .size(300.dp)

            ) {
                item {
                    MarkdownText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        markdown = changelog,
                        textAlign = TextAlign.Justify,
                        color = contraryColor(),
                        onLinkClicked = { url ->
                            ChromeCustomTabsUtil.openUrl(url)
                        },
                        imageLoader = imageLoader
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp))
            AnimatedVisibility(visible = downloadStatus is UpdateUtil.DownloadStatus.Progress) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp), progress = when (downloadStatus) {
                        is UpdateUtil.DownloadStatus.Progress -> downloadStatus.percent.toFloat() / 100f
                        else -> 0f
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp)
                    .navigationBarsPadding(),
            ) {
                OutlinedButtonWithIcon(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f),
                    onClick = onDismiss,
                    icon = Icons.Outlined.Cancel,
                    text = stringResource(R.string.cancel)
                )
                FilledTonalButtonWithIcon(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f),
                    onClick = onConfirmUpdate,
                    icon = Icons.Outlined.Download,
                    text = stringResource(R.string.update)
                )

            }
        }
    })
}

@OptIn(ExperimentalMaterialApi::class)
fun hideDrawer(scope: CoroutineScope) {
    if (UpdateUtil.updateViewState.value.drawerState.isVisible) {
        scope.launch {
            UpdateUtil.updateViewState.value.drawerState.hide()
        }
    }
}