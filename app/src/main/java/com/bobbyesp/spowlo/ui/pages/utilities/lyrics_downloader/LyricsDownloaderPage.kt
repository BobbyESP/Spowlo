package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bobbyesp.spowlo.features.lyrics_downloader.ui.components.alertDialogs.PermissionNotGranted
import com.bobbyesp.spowlo.features.lyrics_downloader.ui.components.alertDialogs.PermissionType
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.ext.permissions.PermissionRequestHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LyricsDownloaderPage(
    viewModel: LyricsDownloaderPageViewModel
) {
    val storagePermissionState = rememberPermissionState(permission = READ_EXTERNAL_STORAGE)
    val navController = LocalNavController.current

    PermissionRequestHandler(
        permissionState = storagePermissionState,
        deniedContent = { shouldShowRationale ->
            PermissionNotGranted(
                neededPermissions = listOf(PermissionType.READ_EXTERNAL_STORAGE),
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
            LyricsDownloaderPageImpl()
        }
    )
}
@Composable
fun LyricsDownloaderPageImpl() {
    Text(text = "LyricsDownloaderPage")
}