package com.bobbyesp.spowlo.ui.pages

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotify_api.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.getString
import com.bobbyesp.spowlo.utils.SPOTDL
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "InitialEntry"

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalLayoutApi::class, ExperimentalMaterialApi::class
)
@Composable
fun InitialEntry(
    navController: NavHostController,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRootRoute = remember(navBackStackEntry) {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Screen.Home.route
        )
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
    val onBackPressed: () -> Unit = { navController.popBackStack() }

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
                        visible = true,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        NavigationBar(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                                .navigationBarsPadding(),
                        ) {
                            Screen.showInBottomNavigation.forEach { (screen, icon) ->

                                val selected = currentRootRoute.value == screen.route

                                val onClick = remember(selected, navController, screen.route) {
                                    {
                                        if (!selected) {
                                            navController.navigate(screen.route) {
                                                popUpTo(Screen.NavGraph.route) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                                NavigationBarItem(
                                    selected = currentRootRoute.value == screen.route,
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
                                            text = stringResource(screen.title),
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
                    startDestination = Screen.Authorization.route,
                    route = Screen.NavGraph.route
                ) {

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
        if (SPOTDL.getString().isNotEmpty()) return@LaunchedEffect
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                val result = UpdateUtil.updateSpotDL()
                if (result == SpotDL.UpdateStatus.DONE) {
                    ToastUtil.makeToastSuspend(
                        App.context.getString(R.string.spotdl_update_success)
                            .format(SPOTDL.getString())
                    )
                }
            }
        }
    }
}

//TODO: Separate the SettingsPage into a different NavGraph (like Seal)