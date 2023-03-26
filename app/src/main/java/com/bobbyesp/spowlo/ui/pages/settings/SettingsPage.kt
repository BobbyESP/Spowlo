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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Aod
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.rounded.EnergySavingsLeaf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.PreferencesHintCard
import com.bobbyesp.spowlo.ui.components.SettingTitle
import com.bobbyesp.spowlo.ui.components.SmallTopAppBar
import com.bobbyesp.spowlo.ui.components.settings.SettingsItemNew
import com.bobbyesp.spowlo.ui.pages.settings.about.LocalAsset

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
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(horizontal = 16.dp)
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
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.general_settings), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.general_settings_desc)) },
                    icon = Icons.Filled.SettingsApplications,
                 onClick = {
                    navController.navigate(Route.GENERAL_DOWNLOAD_PREFERENCES) {
                        launchSingleTop = true
                    }
                },
                    addTonalElevation = true,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    highlightIcon = true
                )
            }
            item {
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.spotify_settings), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.spotify_settings_desc)) },
                    icon = LocalAsset(id = R.drawable.spotify_logo),
                    onClick = {
                        navController.navigate(Route.SPOTIFY_PREFERENCES) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }
            item {
                //new settings item for download directory
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.download_directory), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.download_directory_desc)) },
                    icon = Icons.Filled.Folder,
                    onClick = {
                        navController.navigate(Route.DOWNLOAD_DIRECTORY) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }
            item {
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.format), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.format_settings_desc)) },
                    icon = Icons.Filled.AudioFile,
                    onClick = {
                        navController.navigate(Route.DOWNLOAD_FORMAT) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }
            item {
                //rewrite this with new settings item
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.appearance), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.appearance_settings)) },
                    icon = Icons.Filled.Aod,
                    onClick = {
                        navController.navigate(Route.APPEARANCE) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }
            item {
                //Cookies page
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.cookies), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.cookies_desc)) },
                    icon = Icons.Filled.Cookie,
                    onClick = {
                        navController.navigate(Route.COOKIE_PROFILE) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }

            item {
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.documentation), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.documentation_desc)) },
                    icon = Icons.Filled.Help,
                    onClick = {
                        navController.navigate(Route.DOCUMENTATION) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }

            item{
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.updates_channels), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.updates_channels_desc)) },
                    icon = Icons.Filled.Update,
                    onClick = {
                        navController.navigate(Route.UPDATER_PAGE) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true
                )
            }

            item {
                SettingsItemNew(
                    title = { Text(text = stringResource(id = R.string.about), fontWeight = FontWeight.Bold) },
                    description = { Text(text = stringResource(id = R.string.about_page)) },
                    icon = Icons.Filled.Info,
                    onClick = {
                        navController.navigate(Route.ABOUT) {
                            launchSingleTop = true
                        }
                    },
                    addTonalElevation = true,
                    highlightIcon = true,
                    modifier = Modifier.clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}