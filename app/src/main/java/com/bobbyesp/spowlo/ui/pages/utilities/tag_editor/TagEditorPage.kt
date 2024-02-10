package com.bobbyesp.spowlo.ui.pages.utilities.tag_editor

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bobbyesp.spowlo.ui.pages.utilities.MediaStorePage
import com.bobbyesp.spowlo.ui.pages.utilities.MediaStorePageViewModel
import com.bobbyesp.spowlo.utils.ui.permissions.PermissionRequestHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("InlinedApi") //Make the linter shut up kek
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TagEditorPage(
    viewModel: MediaStorePageViewModel
) {
    val currentApiVersion = Build.VERSION.SDK_INT

    val readAudioFiles = when {
        currentApiVersion < Build.VERSION_CODES.Q -> Manifest.permission.READ_EXTERNAL_STORAGE

        currentApiVersion < Build.VERSION_CODES.S -> Manifest.permission.READ_EXTERNAL_STORAGE

        else -> Manifest.permission.READ_MEDIA_AUDIO
    }

    val storagePermissionState = rememberPermissionState(permission = readAudioFiles)
    val navController = LocalNavController.current

    PermissionRequestHandler(permissionState = storagePermissionState,
        deniedContent = { shouldShowRationale ->
            PermissionNotGrantedDialog(
                neededPermissions = listOf(readAudioFiles.toPermissionType()),
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
                        text = Route.TagEditor.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                subtitle = {
                    MarqueeText(
                        text = stringResource(id = R.string.tag_editor_subtitle),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        fontWeight = FontWeight.Normal
                    )
                },
                onItemClicked = { song ->
                    val artistsList = song.artist.toList()
                    val mainArtist = artistsList.first().toString()

                    val selectedSongParcel = SelectedSong(
                        name = song.title,
                        mainArtist = mainArtist,
                        localSongPath = song.path,
                        artworkPath = song.albumArtPath,
                        fileName = song.fileName
                    )

                    navController.navigate(
                        Route.TagEditor.Editor.createRoute(
                            selectedSongParcel
                        )
                    )
                }
            )
        })
}