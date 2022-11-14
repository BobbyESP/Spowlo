package com.bobbyesp.spowlo.presentation.ui.pages

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.presentation.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.bobbyesp.spowlo.presentation.ui.common.animatedComposable
import com.bobbyesp.spowlo.presentation.ui.components.UpdateDialog
import com.bobbyesp.spowlo.presentation.ui.pages.home.HomePage
import com.bobbyesp.spowlo.presentation.ui.pages.home.HomeViewModel
import com.bobbyesp.spowlo.presentation.ui.pages.placeholders.PagePlaceholder
import com.bobbyesp.spowlo.presentation.ui.pages.settings.SettingsPage
import com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence.AppearancePreferences
import com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence.DarkThemePreferences
import com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence.LanguagesPreferences
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.UpdateUtil
import com.bobbyesp.spowlo.util.Utils
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.presentation.ui.components.BottomNavBar.BottomNavBar
import com.bobbyesp.spowlo.presentation.ui.components.BottomNavBar.NavBarItem

private const val TAG = "InitialEntry"

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InitialEntry(homeViewModel: HomeViewModel, modifier: Modifier = Modifier, navController: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var updateJob: Job? = null
    val onBackPressed = { navController.popBackStack() }
    var showUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var currentDownloadStatus by remember { mutableStateOf(UpdateUtil.DownloadStatus.NotYet as UpdateUtil.DownloadStatus) }
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

    val viewState = homeViewModel.stateFlow.collectAsState()

    Box(modifier = modifier){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
        ){
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
                startDestination = Route.HOME)  {

                animatedComposable(Route.HOME){
                    HomePage(navController = navController, homeViewModel = homeViewModel)
                    if (!viewState.value.loaded){
                        homeViewModel.setup()
                    }
                }

                animatedComposable(Route.SETTINGS){
                    SettingsPage(navController)
                }

                animatedComposable(Route.ABOUT){
                }

                animatedComposable(Route.DISPLAY_SETTINGS){
                    AppearancePreferences(navController)
                }

                animatedComposable(Route.LANGUAGES){
                    LanguagesPreferences{
                        onBackPressed()
                    }
                }

                animatedComposable(Route.DARK_THEME_SELECTOR){
                    DarkThemePreferences {
                        onBackPressed()
                    }
                }

            }
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            kotlin.runCatching {
                val temp = UpdateUtil.checkForUpdate()
                if (temp != null) {
                    latestRelease = temp
                    showUpdateDialog = true
                }
            }.onFailure {
                it.printStackTrace()
            }

        }
    }

    if (showUpdateDialog) {
        UpdateDialog(
            onDismissRequest = {
                showUpdateDialog = false
                updateJob?.cancel()
            },
            title = latestRelease.name.toString(),
            onConfirmUpdate = {
                updateJob = scope.launch(Dispatchers.IO) {
                    kotlin.runCatching {
                        UpdateUtil.downloadApk(latestRelease = latestRelease)
                            .collect { downloadStatus ->
                                currentDownloadStatus = downloadStatus
                                if (downloadStatus is UpdateUtil.DownloadStatus.Finished) {
                                    launcher.launch(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                                }
                            }
                    }.onFailure {
                        it.printStackTrace()
                        currentDownloadStatus = UpdateUtil.DownloadStatus.NotYet
                        Utils.makeToastSuspend(context.getString(R.string.app_update_failed))
                    }
                }
            },
            releaseNote = latestRelease.body.toString(),
            downloadStatus = currentDownloadStatus
        )
    }
}