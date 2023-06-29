package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bobbyesp.spowlo.features.lyrics_downloader.ui.components.alertDialogs.PermissionNotGranted
import com.bobbyesp.spowlo.features.lyrics_downloader.ui.components.alertDialogs.toPermissionType
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.ext.permissions.PermissionRequestHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("InlinedApi") //Make the linter shut up kek
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LyricsDownloaderPage(
    viewModel: LyricsDownloaderPageViewModel
) {
    val currentApiVersion = Build.VERSION.SDK_INT

    val targetPermission = when {
        currentApiVersion < Build.VERSION_CODES.Q ->
            READ_EXTERNAL_STORAGE
        currentApiVersion < Build.VERSION_CODES.S ->
            READ_EXTERNAL_STORAGE
        else -> READ_MEDIA_AUDIO
    }

    val storagePermissionState = rememberPermissionState(permission = targetPermission)
    val navController = LocalNavController.current

    PermissionRequestHandler(
        permissionState = storagePermissionState,
        deniedContent = { shouldShowRationale ->
            PermissionNotGranted(
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
            LyricsDownloaderPageImpl()
        }
    )
}
@Composable
fun LyricsDownloaderPageImpl() {
    Text(text = "LyricsDownloaderPage")
}