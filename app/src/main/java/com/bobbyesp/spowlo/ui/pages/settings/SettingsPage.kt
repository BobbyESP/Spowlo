package com.bobbyesp.spowlo.ui.pages.settings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.MATCH_ALL
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Aod
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.rounded.EnergySavingsLeaf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.PreferencesHintCard
import com.bobbyesp.spowlo.ui.components.SettingItem
import com.bobbyesp.spowlo.ui.components.SettingTitle
import com.bobbyesp.spowlo.ui.components.SmallTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController) {
    val context = LocalContext.current
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var showBatteryHint by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                !pm.isIgnoringBatteryOptimizations(context.packageName)
            } else {
                false
            }
        )
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showBatteryHint = !pm.isIgnoringBatteryOptimizations(context.packageName)
            }
        }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()


    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                titleText = stringResource(id = R.string.settings),
                navigationIcon = { BackButton { navController.popBackStack() } },
                scrollBehavior = scrollBehavior
            )
        }) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            item {
                SettingTitle(text = stringResource(id = R.string.settings))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.packageManager.queryIntentActivities(
                        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
                        MATCH_ALL
                    ).isNotEmpty()
                )
                    item {
                        AnimatedVisibility(
                            visible = showBatteryHint, exit = shrinkVertically() + fadeOut()
                        ) {
                            PreferencesHintCard(
                                title = stringResource(R.string.battery_configuration),
                                icon = Icons.Rounded.EnergySavingsLeaf,
                                description = stringResource(R.string.battery_configuration_desc),
                                isDarkTheme = LocalDarkTheme.current.isDarkTheme()
                            ) {
                                launcher.launch(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                })
                                showBatteryHint =
                                    !pm.isIgnoringBatteryOptimizations(context.packageName)
                            }
                        }
                    }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.general_settings),
                    description = stringResource(
                        id = R.string.general_settings_desc
                    ),
                    icon = Icons.Filled.SettingsApplications
                ) {
                    navController.navigate(Route.GENERAL_DOWNLOAD_PREFERENCES) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.spotify_settings),
                    description = stringResource(
                        id = R.string.spotify_settings_desc
                    ),
                    icon = ImageVector.vectorResource(id = R.drawable.spotify_logo)
                ) {
                    navController.navigate(Route.SPOTIFY_PREFERENCES) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.download_directory),
                    description = stringResource(
                        id = R.string.download_directory_desc
                    ),
                    icon = Icons.Filled.Folder
                ) {
                    navController.navigate(Route.DOWNLOAD_DIRECTORY) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.format),
                    description = stringResource(id = R.string.format_settings_desc),
                    icon = Icons.Filled.AudioFile
                ) {
                    navController.navigate(Route.DOWNLOAD_FORMAT) {
                        launchSingleTop = true
                    }
                }
            }
            /*            item {
                            SettingItem(
                                title = stringResource(id = R.string.subtitle), description = stringResource(
                                    id = R.string.subtitle_desc
                                ), icon = Icons.Outlined.Subtitles
                            ) {
                                navController.navigate(Route.SUBTITLE_PREFERENCES) {
                                    launchSingleTop = true
                                }
                            }
                        }*/
            /*item {
                SettingItem(
                    title = stringResource(id = R.string.network),
                    description = stringResource(id = R.string.network_settings_desc),
                    icon = if (App.connectivityManager.isActiveNetworkMetered) Icons.Filled.SignalCellular4Bar else Icons.Filled.SignalWifi4Bar
                ) {
                    navController.navigate(Route.NETWORK_PREFERENCES) {
                        launchSingleTop = true
                    }
                }
            }*/
            /*item {
                SettingItem(
                    title = stringResource(id = R.string.custom_command),
                    description = stringResource(id = R.string.custom_command_desc),
                    icon = Icons.Outlined.Terminal
                ) {
                    navController.navigate(Route.TEMPLATE) {
                        launchSingleTop = true
                    }
                }
            }*/
            item {
                SettingItem(
                    title = stringResource(id = R.string.appearence), description = stringResource(
                        id = R.string.appearance_settings
                    ), icon = Icons.Filled.Aod
                ) {
                    navController.navigate(Route.APPEARANCE) { launchSingleTop = true }
                }
            }
            item {
                //Cookies page
                SettingItem(
                    title = stringResource(id = R.string.cookies), description = stringResource(
                        id = R.string.cookies_desc
                    ), icon = Icons.Outlined.Cookie
                ) {
                    navController.navigate(Route.COOKIE_PROFILE) { launchSingleTop = true }
                }
            }

            item {
                SettingItem(title = stringResource(id = R.string.documentation), description = stringResource(
                    id = R.string.documentation_desc
                ), icon = Icons.Filled.Help ) {
                    navController.navigate(Route.DOCUMENTATION) { launchSingleTop = true }
                }
            }

            item{
                SettingItem(
                    title = stringResource(id = R.string.updates_channels), description = stringResource(
                        id = R.string.updates_channels_desc
                    ), icon = Icons.Filled.Update
                ) {
                    navController.navigate(Route.UPDATER_PAGE) { launchSingleTop = true }
                }
            }

            item {
                SettingItem(
                    title = stringResource(id = R.string.about), description = stringResource(
                        id = R.string.about_page
                    ), icon = Icons.Filled.Info
                ) {
                    navController.navigate(Route.ABOUT) { launchSingleTop = true }
                }
            }
        }
    }
}