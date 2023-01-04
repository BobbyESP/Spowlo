package com.bobbyesp.spowlo.presentation.ui.pages.downloader

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bobbyesp.library.dto.Song
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.Spowlo
import com.bobbyesp.spowlo.presentation.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.presentation.ui.components.SongCard
import com.bobbyesp.spowlo.presentation.ui.pages.utilities.StateHolder
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.Utils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalLifecycleComposeApi::class
)
fun DownloaderPage(
    navigateToSettings: () -> Unit = {},
    navigateToDownloads: () -> Unit = {},
    navigateToPlaylistPage: () -> Unit = {},
    navigateToFormatPage: () -> Unit = {},
    downloaderViewModel: DownloaderViewModel
){
    val storagePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) { b: Boolean ->
        if (b) {
            downloaderViewModel.startDownloadVideo()
        } else {
            Utils.makeToast(Spowlo.context.getString(R.string.permission_denied))
        }
    }

    val scope = rememberCoroutineScope()
    val downloaderState = StateHolder.downloaderState.collectAsStateWithLifecycle().value
    val taskState = StateHolder.taskState.collectAsStateWithLifecycle().value
    val viewState = downloaderViewModel.viewStateFlow.collectAsStateWithLifecycle().value

    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val useDialog = LocalWindowWidthState.current != WindowWidthSizeClass.Compact

    val checkPermissionOrDownload = {
        if (Build.VERSION.SDK_INT > 29 || storagePermission.status == PermissionStatus.Granted) downloaderViewModel.startDownloadVideo()
        else {
            storagePermission.launchPermissionRequest()
        }
    }
    val downloadCallback = {
        if (PreferencesUtil.getValue(PreferencesUtil.CONFIGURE, true)) downloaderViewModel.showDialog(
            scope,
            useDialog
        )
        else checkPermissionOrDownload()
        keyboardController?.hide()
    }

    BackHandler(viewState.drawerState.isVisible) {
        downloaderViewModel.hideDialog(scope, useDialog)
    }

    if (viewState.isUrlSharingTriggered) {
        downloaderViewModel.onShareIntentConsumed()
        downloadCallback()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ){
        DownloaderPageImpl(downloaderState = downloaderState, taskState = taskState, viewState = viewState) {

            
        }
    }

}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
)
@Composable
fun DownloaderPageImpl(
    downloaderState: StateHolder.DownloaderState,
    taskState: StateHolder.DownloadTaskItem,
    viewState: DownloaderViewModel.ViewState,
    showDownloadProgress: Boolean = false,
    downloadCallback: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToDownloads: () -> Unit = {},
    pasteCallback: () -> Unit = {},
    cancelCallback: () -> Unit = {},
    onVideoCardClicked: () -> Unit = {},
    onUrlChanged: (String) -> Unit = {},
    isPreview: Boolean = false,
    content: @Composable () -> Unit
){
    val hapticFeedback = LocalHapticFeedback.current

    with(downloaderState) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {}, modifier = Modifier.padding(horizontal = 8.dp), navigationIcon = {
                IconButton(onClick = { navigateToSettings() }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }, actions = {
                IconButton(onClick = { navigateToDownloads() }) {
                    Icon(
                        imageVector = Icons.Outlined.Subscriptions,
                        contentDescription = stringResource(id = R.string.downloads_history)
                    )
                }
            })
        }, floatingActionButton = {
            /*FABs(
                modifier = with(receiver = Modifier) { if (showDownloadProgress) this else this.imePadding() },
                downloadCallback = downloadCallback,
                pasteCallback = pasteCallback
            )*/
        }){
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ){
                Column(
                    Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp)
                ){
                    with(taskState){

                    }
                }
            }
        }
    }
    SongCard(
        song = Song(
            "mariposas",
            listOf("sangiovanni"),
            "sangiovanni",
            "url",
            "",
            emptyList(),
            0,
            1,
            17.8,
            2022,
            "",
            0,
            0,
            "",
            false,
            "Quevedo",
            "url",
            "url",
            "https://i.scdn.co/image/ab67616d0000b2730da5b28d9dfe894de5da63ff",
            "",
            "",
            null,
            null,
            null,
        ),
        isExplicit = false,
        isLyrics = true
    )
}