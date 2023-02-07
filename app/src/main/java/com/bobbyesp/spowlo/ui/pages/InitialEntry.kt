package com.bobbyesp.spowlo.ui.pages

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.animatedComposable
import com.bobbyesp.spowlo.ui.common.slideInVerticallyComposable
import com.bobbyesp.spowlo.ui.pages.dialogs.UpdateDialogImpl
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderPage
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderViewModel
import com.bobbyesp.spowlo.ui.pages.mod_downloader.ModsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.mod_downloader.ModsDownloaderViewModel
import com.bobbyesp.spowlo.ui.pages.playlist.PlaylistMetadataPage
import com.bobbyesp.spowlo.ui.pages.settings.SettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.appearance.AppearancePage
import com.bobbyesp.spowlo.ui.pages.settings.directories.DownloadsDirectoriesPage
import com.bobbyesp.spowlo.ui.pages.settings.format.SettingsFormatsPage
import com.bobbyesp.spowlo.ui.pages.settings.general.GeneralSettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifySettingsPage
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "InitialEntry"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InitialEntry(
    downloaderViewModel: DownloaderViewModel,
    modsDownloaderViewModel: ModsDownloaderViewModel,
    isUrlShared: Boolean
) {
    val navController = rememberAnimatedNavController()
    val context = LocalContext.current
    var showUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var currentDownloadStatus by remember { mutableStateOf(UpdateUtil.DownloadStatus.NotYet as UpdateUtil.DownloadStatus) }
    val scope = rememberCoroutineScope()
    var updateJob: Job? = null
    var latestRelease by remember { mutableStateOf(UpdateUtil.LatestRelease()) }
    val settings =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            UpdateUtil.installLatestApk()
        }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            UpdateUtil.installLatestApk()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls())
                    settings.launch(
                        Intent(
                            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:${context.packageName}"),
                        )
                    )
                else
                    UpdateUtil.installLatestApk()
            }
        }
    }

    val onBackPressed: () -> Unit = { navController.popBackStack() }

    if (isUrlShared) {
        if (navController.currentDestination?.route != Route.HOME) {
            navController.popBackStack(route = Route.HOME, inclusive = false, saveState = true)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxWidth(
                    when (LocalWindowWidthState.current) {
                        WindowWidthSizeClass.Compact -> 1f
                        WindowWidthSizeClass.Expanded -> 0.5f
                        else -> 0.8f
                    }
                )
                .align(Alignment.Center),
            navController = navController,
            startDestination = Route.HOME
        ) {
            //TODO: Add all routes
            animatedComposable(Route.HOME) { //TODO: Change this route to Route.DOWNLOADER, but by now, keep it as Route.HOME
                DownloaderPage(
                    navigateToDownloads = { navController.navigate(Route.DOWNLOADS_HISTORY) },
                    navigateToSettings = { navController.navigate(Route.SETTINGS) },
                    navigateToPlaylistPage = { navController.navigate(Route.PLAYLIST) },
                    onSongCardClicked = {
                        navController.navigate(Route.PLAYLIST_METADATA_PAGE)
                    },
                    onNavigateToTaskList = { navController.navigate(Route.TASK_LIST) },
                    downloaderViewModel = downloaderViewModel
                )
            }
            animatedComposable(Route.SETTINGS) {
                SettingsPage(
                    navController = navController
                )
            }
            animatedComposable(Route.GENERAL_DOWNLOAD_PREFERENCES) {
                GeneralSettingsPage(
                    onBackPressed = onBackPressed
                )
            }
            animatedComposable(Route.DOWNLOADS_HISTORY) {

            }
            animatedComposable(Route.DOWNLOAD_DIRECTORY) {
                DownloadsDirectoriesPage {
                    onBackPressed()
                }
            }
            animatedComposable(Route.APPEARANCE) {
                AppearancePage(navController = navController)
            }
            animatedComposable(Route.DOWNLOAD_FORMAT) {
                SettingsFormatsPage {
                    onBackPressed()
                }
            }
            animatedComposable(Route.SPOTIFY_PREFERENCES) {
                SpotifySettingsPage {
                    onBackPressed()
                }
            }
            slideInVerticallyComposable(Route.PLAYLIST_METADATA_PAGE) {
                PlaylistMetadataPage(
                    onBackPressed,
                    //TODO: ADD THE ABILITY TO PASS JUST SONGS AND NOT GET THEM FROM THE MUTABLE STATE
                )
            }
            animatedComposable(Route.MODS_DOWNLOADER) {
                ModsDownloaderPage(
                    onBackPressed,
                    modsDownloaderViewModel
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            runCatching {
                //TODO: Add check for updates of spotDL
                UpdateUtil.checkForUpdate()?.let {
                    latestRelease = it
                    showUpdateDialog = true
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    if (showUpdateDialog) {
        UpdateDialogImpl(
            onDismissRequest = {
                showUpdateDialog = false
                updateJob?.cancel()
            },
            title = latestRelease.name.toString(),
            onConfirmUpdate = {
                updateJob = scope.launch(Dispatchers.IO) {
                    runCatching {
                        UpdateUtil.downloadApk(latestRelease = latestRelease)
                            .collect { downloadStatus ->
                                currentDownloadStatus = downloadStatus
                                if (downloadStatus is UpdateUtil.DownloadStatus.Finished) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        launcher.launch(Manifest.permission.REQUEST_INSTALL_PACKAGES)
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
            releaseNote = latestRelease.body.toString(),
            downloadStatus = currentDownloadStatus
        )
    }

}