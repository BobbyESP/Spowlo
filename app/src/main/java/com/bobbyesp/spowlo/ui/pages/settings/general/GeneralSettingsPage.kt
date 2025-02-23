package com.bobbyesp.spowlo.ui.pages.settings.general

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.PrintDisabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.booleanState
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.ElevatedSettingsCard
import com.bobbyesp.spowlo.ui.components.settings.SettingsItemNew
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.ui.dialogs.NotificationPermissionDialog
import com.bobbyesp.spowlo.ui.dialogs.bottomsheets.getString
import com.bobbyesp.spowlo.utils.CONFIGURE
import com.bobbyesp.spowlo.utils.DEBUG
import com.bobbyesp.spowlo.utils.INCOGNITO_MODE
import com.bobbyesp.spowlo.utils.NOTIFICATION
import com.bobbyesp.spowlo.utils.NotificationsUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.PreferencesUtil.getString
import com.bobbyesp.spowlo.utils.PreferencesUtil.updateBoolean
import com.bobbyesp.spowlo.utils.SPOTDL
import com.bobbyesp.spowlo.utils.ToastUtil
import com.bobbyesp.spowlo.utils.UpdateUtil
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class, ExperimentalPermissionsApi::class)
@Composable
fun GeneralSettingsPage(
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })

    var displayErrorReport by DEBUG.booleanState

    var useNotifications by remember {
        mutableStateOf(
            PreferencesUtil.getValue(NOTIFICATION)
        )
    }
    var isUpdatingLib by remember { mutableStateOf(false) }

    val loadingString = App.context.getString(R.string.loading)

    var spotDLVersion by remember {
        mutableStateOf(
            loadingString
        )
    }

    var configureBeforeDownload by remember {
        mutableStateOf(
            PreferencesUtil.getValue(CONFIGURE)
        )
    }

    var incognitoMode by remember {
        mutableStateOf(
            PreferencesUtil.getValue(INCOGNITO_MODE)
        )
    }

    var showNotificationDialog by remember {mutableStateOf(false)}
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    val isNotificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermission?.status is PermissionStatus.Granted
            } else {
                false
            }
        )
    }

    //create a non-blocking coroutine to get the version
    LaunchedEffect(Unit) {
        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    spotDLVersion = SpotDL.getInstance().version(appContext = App.context)
                        ?: getString(R.string.unknown)
                }
            } catch (e: Exception) {
                spotDLVersion = e.message ?: e.toString()
            }

        }
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.general), fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                BackButton { onBackPressed() }
            }, scrollBehavior = scrollBehavior
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                /* item {
                    WarningCard(
                        modifier = Modifier.padding(vertical = 10.dp),
                        title = stringResource(id = R.string.updates_blocked),
                        warningText = stringResource(
                            id = R.string.updates_blocked_description
                        )
                    )
                } */
                item {
                    ElevatedSettingsCard {
                        SettingsItemNew(
                            onClick = {
                                /*val spotDLNewVersion = SpotDL.getInstance().version(appContext = App.context) ?: getString(R.string.unknown)
                                var spotDLVersion = SPOTDL.getString()
                                if (spotDLNewVersion != spotDLVersion) {
                                    scope.launch {
                                        runCatching {
                                            isUpdatingLib = true
                                            UpdateUtil.updateSpotDL()
                                            spotDLVersion = SPOTDL.getString()
                                        }.onFailure { e ->
                                            e.printStackTrace()
                                            ToastUtil.makeToastSuspend(App.context.getString(R.string.spotdl_update_failed))
                                        }.onSuccess {
                                            ToastUtil.makeToastSuspend(
                                                App.context.getString(R.string.spotdl_update_success)
                                                    .format(spotDLVersion)
                                            )
                                        }
                                    }
                                } else {
                                    ToastUtil.makeToast(App.context.getString(R.string.spotDl_uptodate))
                                }*/
                                scope.launch {
                                    runCatching {
                                        isUpdatingLib = true
                                        UpdateUtil.updateSpotDL()
                                        spotDLVersion = SPOTDL.getString()
                                    }.onFailure { e ->
                                        e.printStackTrace()
                                        ToastUtil.makeToastSuspend(App.context.getString(R.string.spotdl_update_failed))
                                    }.onSuccess {
                                        ToastUtil.makeToastSuspend(
                                            App.context.getString(R.string.spotdl_update_success)
                                                .format(spotDLVersion)
                                        )
                                    }
                                }
                            },
                            title = {
                                Text(
                                    text = stringResource(id = R.string.spotdl_version),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = Icons.Outlined.Info,
                            description = { Text(text = spotDLVersion) }
                        )
                    }
                }

                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.general_settings))
                }
                item{
                    SettingsSwitch(
                        onCheckedChange = {
                            displayErrorReport = !displayErrorReport
                            PreferencesUtil.updateValue(DEBUG, displayErrorReport)
                        },
                        checked = displayErrorReport,
                        title = {
                            Text(
                                text = stringResource(R.string.print_details),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = if (displayErrorReport) Icons.Outlined.Print else Icons.Outlined.PrintDisabled,
                        description = { Text(text = stringResource(R.string.print_details_desc)) },
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = 8.dp, topEnd = 8.dp,
                                bottomEnd = 0.dp, bottomStart = 0.dp
                            )
                        ),
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            if (notificationPermission?.status is PermissionStatus.Denied) {
                                showNotificationDialog = true
                            } else if (isNotificationPermissionGranted) {
                                if (useNotifications)
                                    NotificationsUtil.cancelAllNotifications()
                                useNotifications = !useNotifications
                                PreferencesUtil.updateValue(
                                    NOTIFICATION, useNotifications
                                )

                            }
                        },
                        checked = useNotifications && isNotificationPermissionGranted,
                        title = {
                            Text(
                                text = stringResource(R.string.use_notifications),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = if (useNotifications) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff,
                        description = {
                            Text(text = stringResource(R.string.use_notifications_desc))
                        },
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            incognitoMode = !incognitoMode
                            PreferencesUtil.updateValue(INCOGNITO_MODE, incognitoMode)
                        },
                        checked = incognitoMode,
                        title = {
                            Text(
                                text = stringResource(R.string.incognito_mode),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = if (incognitoMode) Icons.Outlined.HistoryToggleOff else Icons.Outlined.History,
                        description = {
                            Text(text = stringResource(R.string.incognito_mode_desc))
                        },
                    )
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            configureBeforeDownload = !configureBeforeDownload
                            PreferencesUtil.updateValue(CONFIGURE, configureBeforeDownload)
                        },
                        checked = configureBeforeDownload,
                        title = {
                            Text(
                                text = stringResource(R.string.pre_configure_download),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        icon = Icons.Outlined.Construction,
                        description = {
                            Text(text = stringResource(R.string.pre_configure_download_desc))
                        },
                        clipCorners = false,
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp
                            )
                        ),
                    )
                }
            }
        }
    )
    if (showNotificationDialog) {
        NotificationPermissionDialog(onDismissRequest = {
            showNotificationDialog = false
        }, onPermissionGranted = {
            notificationPermission?.launchPermissionRequest()
            NOTIFICATION.updateBoolean(true)
            useNotifications = true
            showNotificationDialog = false
        })
    }
}