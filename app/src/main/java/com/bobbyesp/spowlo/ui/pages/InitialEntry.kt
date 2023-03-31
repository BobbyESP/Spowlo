package com.bobbyesp.spowlo.ui.pages

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.mod_downloader.data.remote.ModsDownloaderAPI
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.Route.MARKDOWN_VIEWER
import com.bobbyesp.spowlo.ui.common.animatedComposable
import com.bobbyesp.spowlo.ui.common.animatedComposableVariant
import com.bobbyesp.spowlo.ui.common.arg
import com.bobbyesp.spowlo.ui.common.id
import com.bobbyesp.spowlo.ui.common.slideInVerticallyComposable
import com.bobbyesp.spowlo.ui.dialogs.UpdaterBottomDrawer
import com.bobbyesp.spowlo.ui.dialogs.bottomsheets.DownloaderBottomSheet
import com.bobbyesp.spowlo.ui.dialogs.bottomsheets.MoreOptionsHomeBottomSheet
import com.bobbyesp.spowlo.ui.pages.download_tasks.DownloadTasksPage
import com.bobbyesp.spowlo.ui.pages.download_tasks.FullscreenConsoleOutput
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderPage
import com.bobbyesp.spowlo.ui.pages.downloader.DownloaderViewModel
import com.bobbyesp.spowlo.ui.pages.history.DownloadsHistoryPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists.PlaylistPage
import com.bobbyesp.spowlo.ui.pages.metadata_viewer.playlists.PlaylistPageViewModel
import com.bobbyesp.spowlo.ui.pages.mod_downloader.ModsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.mod_downloader.ModsDownloaderViewModel
import com.bobbyesp.spowlo.ui.pages.playlist.PlaylistMetadataPage
import com.bobbyesp.spowlo.ui.pages.searcher.SearcherPage
import com.bobbyesp.spowlo.ui.pages.settings.SettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.about.AboutPage
import com.bobbyesp.spowlo.ui.pages.settings.appearance.AppThemePreferencesPage
import com.bobbyesp.spowlo.ui.pages.settings.appearance.AppearancePage
import com.bobbyesp.spowlo.ui.pages.settings.appearance.LanguagePage
import com.bobbyesp.spowlo.ui.pages.settings.cookies.CookieProfilePage
import com.bobbyesp.spowlo.ui.pages.settings.cookies.CookiesSettingsViewModel
import com.bobbyesp.spowlo.ui.pages.settings.cookies.WebViewPage
import com.bobbyesp.spowlo.ui.pages.settings.directories.DownloadsDirectoriesPage
import com.bobbyesp.spowlo.ui.pages.settings.documentation.DocumentationPage
import com.bobbyesp.spowlo.ui.pages.settings.downloader.DownloaderSettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.format.AudioQualityDialog
import com.bobbyesp.spowlo.ui.pages.settings.format.SettingsFormatsPage
import com.bobbyesp.spowlo.ui.pages.settings.general.GeneralSettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.spotify.SpotifySettingsPage
import com.bobbyesp.spowlo.ui.pages.settings.updater.UpdaterPage
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "InitialEntry"

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun InitialEntry(
    downloaderViewModel: DownloaderViewModel,
    modsDownloaderViewModel: ModsDownloaderViewModel,
    playlistPageViewModel: PlaylistPageViewModel,
    isUrlShared: Boolean
) {
    //bottom sheet remember state
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRootRoute = remember(navBackStackEntry) {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Route.DownloaderNavi
        )
    }
    //navController.currentBackStack.value.getOrNull(1)?.destination?.route
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
    val cookiesViewModel: CookiesSettingsViewModel = viewModel()
    val onBackPressed: () -> Unit = { navController.popBackStack() }

    if (isUrlShared) {
        if (navController.currentDestination?.route != Route.DOWNLOADER) {
            navController.popBackStack(
                route = Route.DOWNLOADER,
                inclusive = false,
                saveState = true
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val navRootUrl = "android-app://androidx.navigation/"
        ModalBottomSheetLayout(
            bottomSheetNavigator,
            sheetShape = MaterialTheme.shapes.medium.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            ),
            scrimColor = MaterialTheme.colorScheme.scrim.copy(0.5f),
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        ) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = !shouldHideBottomNavBar,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        NavigationBar(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                                .navigationBarsPadding(),
                        ) {
                            MainActivity.showInBottomNavigation.forEach { (route, icon) ->
                                val text = when (route) {
                                    Route.DownloaderNavi -> App.context.getString(R.string.downloader)
                                    Route.SearcherNavi -> App.context.getString(R.string.searcher)
                                    Route.DownloadTasksNavi -> App.context.getString(R.string.tasks)
                                    else -> ""
                                }

                                val selected = currentRootRoute.value == route

                                val onClick = remember(selected, navController, route) {
                                    {
                                        if (!selected) {
                                            navController.navigate(route) {
                                                popUpTo(Route.NavGraph) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                                NavigationBarItem(
                                    selected = currentRootRoute.value == route,
                                    onClick = onClick,
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
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    })
                            }
                        }
                    }
                }, modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) { paddingValues ->
                AnimatedNavHost(
                    modifier = Modifier
                        .fillMaxWidth(
                            when (LocalWindowWidthState.current) {
                                WindowWidthSizeClass.Compact -> 1f
                                WindowWidthSizeClass.Expanded -> 1f
                                else -> 0.8f
                            }
                        )
                        .align(Alignment.Center)
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues),
                    navController = navController,
                    startDestination = Route.DownloaderNavi,
                    route = Route.NavGraph
                ) {
                    navigation(startDestination = Route.DOWNLOADER, route = Route.DownloaderNavi) {
                        animatedComposable(Route.DOWNLOADER) {
                            DownloaderPage(
                                navigateToDownloads = {
                                    navController.navigate(Route.DOWNLOADS_HISTORY) {
                                        launchSingleTop = true
                                    }
                                },
                                navigateToSettings = {
                                    navController.navigate(Route.MORE_OPTIONS_HOME) {
                                        launchSingleTop = true
                                    }
                                },
                                navigateToDownloaderSheet = {
                                    navController.navigate(Route.DOWNLOADER_SHEET) {
                                        launchSingleTop = true
                                    }
                                },
                                onSongCardClicked = {
                                    navController.navigate(Route.PLAYLIST_METADATA_PAGE) {
                                        launchSingleTop = true
                                    }
                                },
                                navigateToMods = {
                                    navController.navigate(Route.MODS_DOWNLOADER) {
                                        launchSingleTop = true
                                    }
                                },
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
                                onBackPressed = onBackPressed,
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
                        animatedComposable(Route.DOWNLOADER_SETTINGS) {
                            DownloaderSettingsPage {
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
                        animatedComposable(Route.DOCUMENTATION) {
                            DocumentationPage(
                                onBackPressed,
                                navController
                            )
                        }

                        animatedComposable(Route.ABOUT) {
                            AboutPage {
                                onBackPressed()
                            }
                        }

                        animatedComposable(Route.LANGUAGES) {
                            LanguagePage {
                                onBackPressed()
                            }
                        }


                        navDeepLink {
                            // Want to go to "markdown_viewer/{markdownFileName}"
                            uriPattern =
                                "android-app://androidx.navigation/markdown_viewer/{markdownFileName}"
                        }

                        animatedComposable(
                            MARKDOWN_VIEWER arg "markdownFileName",
                            arguments = listOf(
                                navArgument(
                                    "markdownFileName"
                                ) {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val mdFileName =
                                backStackEntry.arguments?.getString("markdownFileName") ?: ""
                            Log.d("MainActivity", mdFileName)
                            MarkdownViewerPage(
                                markdownFileName = mdFileName,
                                onBackPressed = onBackPressed
                            )
                        }

                        //DIALOGS -------------------------------
                        //TODO: ADD DIALOGS
                        dialog(Route.AUDIO_QUALITY_DIALOG) {
                            AudioQualityDialog(
                                onBackPressed
                            )
                        }

                        //BOTTOM SHEETS --------------------------
                        bottomSheet(Route.MORE_OPTIONS_HOME) {
                            MoreOptionsHomeBottomSheet(
                                onBackPressed,
                                navController
                            )
                        }

                        bottomSheet(Route.DOWNLOADER_SHEET) {
                            DownloaderBottomSheet(
                                onBackPressed,
                                downloaderViewModel,
                                navController
                            )
                        }

                    }

                    //Can add the downloads history bottom sheet here using `val downloadsHistoryViewModel = hiltViewModel()`
                    navigation(startDestination = Route.SEARCHER, route = Route.SearcherNavi) {
                        animatedComposableVariant(Route.SEARCHER) {
                            SearcherPage(
                                navController = navController
                            )
                        }


                        //create a deeplink to the playlist page passing the id of the playlist
                        navDeepLink {
                            // Want to go to "markdown_viewer/{markdownFileName}"
                            uriPattern =
                                StringBuilder().append(navRootUrl).append(Route.PLAYLIST_PAGE)
                                    .append("/{type}")
                                    .append("/{id}").toString()
                        }

                        //We create the arguments for the route
                        val typeArg = navArgument("type") {
                            type = NavType.StringType
                        }

                        val idArg = navArgument("id") {
                            type = NavType.StringType
                        }


                        //We build the route with the type of the destination and the id of it
                        val routeWithIdPattern: String =
                            StringBuilder().append(Route.PLAYLIST_PAGE).append("/{type}")
                                .append("/{id}").toString()

                        //We create the composable with the route and the arguments
                        animatedComposableVariant(
                            routeWithIdPattern,
                            arguments = listOf(typeArg, idArg)
                        ) { backStackEntry ->
                            val id =
                                backStackEntry.arguments?.getString("id") ?: "SOMETHING WENT WRONG"
                            val type = backStackEntry.arguments?.getString("type")
                                ?: "SOMETHING WENT WRONG"

                            PlaylistPage(
                                onBackPressed,
                                id = id,
                                type = type,
                                playlistPageViewModel = playlistPageViewModel,
                            )
                        }
                    }

                    navigation(startDestination = Route.DOWNLOAD_TASKS, route = Route.DownloadTasksNavi) {

                        animatedComposable(
                            Route.FULLSCREEN_LOG arg "taskHashCode",
                            arguments = listOf(navArgument("taskHashCode") {
                                type = NavType.IntType
                            }
                            )) {

                            FullscreenConsoleOutput(
                                onBackPressed = onBackPressed,
                                taskHashCode = it.arguments?.getInt("taskHashCode") ?: -1
                            )
                        }

                        animatedComposable(Route.DOWNLOAD_TASKS) {
                            DownloadTasksPage(
                                onNavigateToDetail = { navController.navigate(Route.FULLSCREEN_LOG id it) }
                            )
                        }
                    }
                }
            }
        }
    }

    //INIT SPOTIFY API
    LaunchedEffect(Unit) {
        runCatching {
            SpotifyApiRequests.provideSpotifyApi()
        }.onFailure {
            it.printStackTrace()
            ToastUtil.makeToastSuspend(context.getString(R.string.spotify_api_error))
        }
    }


    LaunchedEffect(Unit) {
        if (PreferencesUtil.isNetworkAvailable()) launch(Dispatchers.IO) {
            runCatching {
                UpdateUtil.checkForUpdate()?.let {
                    latestRelease = it
                    showUpdateDialog = true
                }
                if (showUpdateDialog) {
                    UpdateUtil.showUpdateDrawer()
                }
            }.onFailure {
                it.printStackTrace()
                ToastUtil.makeToastSuspend(context.getString(R.string.update_check_failed))
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "InitialEntry: Checking for mod updates")
        if (PreferencesUtil.isNetworkAvailable()) ModsDownloaderAPI.getAPIResponse()
            .onSuccess {
                Log.d(TAG, "InitialEntry: Mods API call success")
                modsDownloaderViewModel.updateApiResponse(it)
            }.onFailure {
                ToastUtil.makeToastSuspend(App.context.getString(R.string.api_call_failed))
            }
    }


    if (showUpdateDialog) {
        UpdaterBottomDrawer(latestRelease = latestRelease)
    }

}

//TODO: Separate the SettingsPage into a different NavGraph (like Seal)