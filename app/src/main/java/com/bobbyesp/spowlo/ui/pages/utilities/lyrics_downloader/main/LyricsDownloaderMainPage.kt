package com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.alertDialogs.PermissionNotGrantedDialog
import com.bobbyesp.spowlo.ui.components.alertDialogs.toPermissionType
import com.bobbyesp.spowlo.ui.components.text.MarqueeText
import com.bobbyesp.spowlo.ui.ext.toList
import com.bobbyesp.spowlo.ui.pages.utilities.MediaStorePage
import com.bobbyesp.spowlo.utils.ui.permissions.PermissionRequestHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

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