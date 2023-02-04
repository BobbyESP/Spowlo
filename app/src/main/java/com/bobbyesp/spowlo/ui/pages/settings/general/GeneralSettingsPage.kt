package com.bobbyesp.spowlo.ui.pages.settings.general

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.PrintDisabled
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.LargeTopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.bobbyesp.library.SpotDL
import com.bobbyesp.spowlo.ui.common.booleanState
import com.bobbyesp.spowlo.ui.components.PreferenceItem
import com.bobbyesp.spowlo.ui.components.PreferenceSwitch
import com.bobbyesp.spowlo.utils.CUSTOM_COMMAND
import com.bobbyesp.spowlo.utils.DEBUG
import com.bobbyesp.spowlo.utils.PreferencesUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val isCustomCommandEnabled by remember {
        mutableStateOf(
            PreferencesUtil.getValue(CUSTOM_COMMAND)
        )
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.general_settings)) },
                navigationIcon = {
                    BackButton { onBackPressed() }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item {
                    val spotDLVersion by remember { mutableStateOf(
                        SpotDL.getInstance().version(appContext = App.context)
                    ) }
                    PreferenceItem(
                        title = stringResource(id = R.string.spotdl_version),
                        description = spotDLVersion,
                        icon = Icons.Outlined.Info,
                        onClick = {
                        },
                        onClickLabel = stringResource(id = R.string.update),
                        onLongClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                        }, onLongClickLabel = stringResource(id = R.string.open_settings)
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.print_details),
                        description = stringResource(R.string.print_details_desc),
                        icon = if (displayErrorReport) Icons.Outlined.Print else Icons.Outlined.PrintDisabled,
                        enabled = !isCustomCommandEnabled,
                        onClick = {
                            displayErrorReport = !displayErrorReport
                            PreferencesUtil.updateValue(DEBUG, displayErrorReport)
                        },
                        isChecked = displayErrorReport
                    )
                }
            }
        })
}