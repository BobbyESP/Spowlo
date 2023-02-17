package com.bobbyesp.spowlo.ui.pages

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.animatedComposable
import com.bobbyesp.spowlo.ui.common.slideInVerticallyComposable
import com.bobbyesp.spowlo.ui.dialogs.UpdateDialogImpl
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderPage
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderViewModel
import com.bobbyesp.spowlo.ui.pages.history.DownloadsHistoryPage
import com.bobbyesp.spowlo.ui.pages.mod_downloader.ModsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.mod_downloader.ModsDownloaderViewModel
import com.bobbyesp.spowlo.ui.pages.playlist.PlaylistMetadataPage
import com.bobbyesp.spowlo.ui.pages.settings.SettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.appearance.AppThemePreferencesPage
import com.bobbyesp.spowlo.ui.pages.settings.appearance.AppearancePage
import com.bobbyesp.spowlo.ui.pages.settings.cookies.CookieProfilePage
import com.bobbyesp.spowlo.ui.pages.settings.cookies.CookiesSettingsViewModel
import com.bobbyesp.spowlo.ui.pages.settings.cookies.WebViewPage
import com.bobbyesp.spowlo.ui.pages.settings.directories.DownloadsDirectoriesPage
import com.bobbyesp.spowlo.ui.pages.settings.format.SettingsFormatsPage
import com.bobbyesp.spowlo.ui.pages.settings.general.GeneralSettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifySettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.updater.UpdaterPage
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "InitialEntry"

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun InitialEntry(
    downloaderViewModel: DownloaderViewModel,
    modsDownloaderViewModel: ModsDownloaderViewModel,
    isUrlShared: Boolean
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRootRoute = remember(navBackStackEntry) {
        navController.backQueue.getOrNull(1)?.destination?.route
    }
    val shouldHideBottomNavBar = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.hierarchy?.any { it.route == Route.SPOTIFY_SETUP } == true
    }

    val isLandscape = remember { MutableTransitionState(false) }

    val windowWidthState = LocalWindowWidthState.current

    LaunchedEffect(windowWidthState) {
        isLandscape.targetState = windowWidthState == WindowWidthSizeClass.Expanded
    }

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

    //When the app is opened, call xManagerApi to check for updates
    //WARN: DISABLED FOR PETITION OF THE xManager TEAM
    /*LaunchedEffect(Unit) {
        xManagerAPI.getPackagesResponseDto().onFailure {
            ToastUtil.makeToastSuspend(App.context.getString(R.string.error_checking_for_updates))
        }.onSuccess {
            modsDownloaderViewModel.updateApiResponse(it)
        }
    }*/


    val cookiesViewModel: CookiesSettingsViewModel = viewModel()
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
        /*Scaffold(
            bottomBar = {
                    NavigationBar(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                            .navigationBarsPadding(),
                    ) {
                        MainActivity.showInBottomNavigation.forEach() { (route, icon) ->
                            val text = when (route) {
                                Route.HOME -> App.context.getString(R.string.downloader)
                                Route.SEARCHER -> App.context.getString(R.string.searcher)
                                Route.MEDIA_PLAYER -> App.context.getString(R.string.mediaplayer)
                                else -> ""
                            }
                            NavigationBarItem(selected = currentRootRoute == route,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                label = {
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                })
                        }
                    }
                }, modifier = Modifier.fillMaxSize().align(Alignment.Center)) { paddingValues ->*/
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxWidth(
                    when (LocalWindowWidthState.current) {
                        WindowWidthSizeClass.Compact -> 1f
                        WindowWidthSizeClass.Expanded -> 0.5f
                        else -> 0.8f
                    }
                )
                .align(Alignment.Center)
                .padding(/*bottom = paddingValues.calculateBottomPadding()*/),
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
                    navigateToMods = { navController.navigate(Route.MODS_DOWNLOADER) },
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
                DownloadsHistoryPage(
                    onBackPressed = onBackPressed
                )
            }
            animatedComposable(Route.DOWNLOAD_DIRECTORY) {
                DownloadsDirectoriesPage {
                    onBackPressed()
                }
            }
            animatedComposable(Route.APPEARANCE) {
                AppearancePage(navController = navController)
            }
            animatedComposable(Route.APP_THEME) {
                AppThemePreferencesPage {
                    onBackPressed()
                }
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
            animatedComposable(Route.COOKIE_PROFILE) {
                CookieProfilePage(
                    cookiesViewModel = cookiesViewModel,
                    navigateToCookieGeneratorPage = { navController.navigate(Route.COOKIE_GENERATOR_WEBVIEW) },
                ) { onBackPressed() }
            }
            animatedComposable(
                Route.COOKIE_GENERATOR_WEBVIEW
            ) {
                WebViewPage(cookiesViewModel) { onBackPressed() }
            }
            animatedComposable(Route.UPDATER_PAGE) {
                UpdaterPage(
                    onBackPressed
                )
            }
        }
    }
//}

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

@OptIn(ExperimentalAnimationApi::class)
private fun buildAnimationForward(scope: AnimatedContentScope<NavBackStackEntry>): Boolean {
    val isRoute = getStartingRoute(scope.initialState.destination)
    val tsRoute = getStartingRoute(scope.targetState.destination)

    val isIndex = MainActivity.showInBottomNavigation.keys.indexOfFirst { it == isRoute }
    val tsIndex = MainActivity.showInBottomNavigation.keys.indexOfFirst { it == tsRoute }

    return tsIndex == -1 || isRoute == tsRoute || tsIndex > isIndex
}

private fun getStartingRoute(destination: NavDestination): String {
    return destination.hierarchy.toList().let { it[it.lastIndex - 1] }.route.orEmpty()
}

//TODO: Separate the SettingsPage into a different NavGraph (like Seal)