package com.bobbyesp.spowlo.ui.pages.settings.general

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
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
import com.bobbyesp.library.SpotDLRequest
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.booleanState
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.ElevatedSettingsCard
import com.bobbyesp.spowlo.ui.components.settings.SettingsItemNew
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.utils.CONFIGURE
import com.bobbyesp.spowlo.utils.DEBUG
import com.bobbyesp.spowlo.utils.NOTIFICATION
import com.bobbyesp.spowlo.utils.PreferencesUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
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

    //create a non-blocking coroutine to get the version
    LaunchedEffect(Unit) {
        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    spotDLVersion = SpotDL.getInstance()
                        .execute(SpotDLRequest().addOption("-v"), null, null).output
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
                item {
                    ElevatedSettingsCard {
                        SettingsItemNew(onClick = { },
                            title = {
                                Text(
                                    text = stringResource(id = R.string.spotdl_version),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = Icons.Outlined.Info,
                            description = { Text(text = spotDLVersion) })

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
                        )
                    }
                }

                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.general_settings))
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            useNotifications = !useNotifications
                            PreferencesUtil.updateValue(NOTIFICATION, useNotifications)
                        },
                        checked = useNotifications,
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
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = 8.dp, topEnd = 8.dp
                            )
                        ),
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
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp
                            )
                        ),
                    )
                }
            }
        })
}