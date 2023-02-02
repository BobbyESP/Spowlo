package com.bobbyesp.spowlo.ui.pages.downloader

import android.Manifest
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.utils.ToastUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class
)
fun DownloaderPage(
    navigateToSettings: () -> Unit = {},
    navigateToDownloads: () -> Unit = {},
    navigateToPlaylistPage: () -> Unit = {},
    onNavigateToTaskList: () -> Unit = {},
    downloaderViewModel: DownloaderViewModel = hiltViewModel(),
){
    val storagePermission = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) { b: Boolean ->
        if (b) {
            //downloaderViewModel.startDownloadSong()
        } else {
            ToastUtil.makeToast(R.string.permission_denied)
        }
    }

    val checkPermissionOrDownload = {
        if (Build.VERSION.SDK_INT > 29 || storagePermission.status == PermissionStatus.Granted)
            //downloaderViewModel.startDownloadSong()
        else {
            storagePermission.launchPermissionRequest()
        }
    }

    val scope = rememberCoroutineScope()
}