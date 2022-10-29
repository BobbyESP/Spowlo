package com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.presentation.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.presentation.ui.components.BackButton
import com.bobbyesp.spowlo.presentation.ui.components.PreferenceSingleChoiceItem
import com.bobbyesp.spowlo.presentation.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.presentation.ui.components.PreferenceSwitch
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.bobbyesp.spowlo.util.PreferencesUtil.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.bobbyesp.spowlo.util.PreferencesUtil.DarkThemePreference.Companion.OFF
import com.bobbyesp.spowlo.util.PreferencesUtil.DarkThemePreference.Companion.ON
import com.bobbyesp.spowlo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePreferences(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val darkThemePreference = LocalDarkTheme.current
    val isHighContrastModeEnabled = darkThemePreference.isHighContrastModeEnabled
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.dark_theme),
                    )
                }, navigationIcon = {
                    BackButton(modifier = Modifier.padding(start = 8.dp)) {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.follow_system),
                        selected = darkThemePreference.darkThemeValue == FOLLOW_SYSTEM
                    ) { PreferencesUtil.modifyDarkThemePreference(FOLLOW_SYSTEM) }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.on),
                        selected = darkThemePreference.darkThemeValue == ON
                    ) { PreferencesUtil.modifyDarkThemePreference(ON) }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.off),
                        selected = darkThemePreference.darkThemeValue == OFF
                    ) { PreferencesUtil.modifyDarkThemePreference(OFF) }
                }
                item {
                    PreferenceSubtitle(text = stringResource(R.string.additional_settings))
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.high_contrast),
                        icon = Icons.Outlined.Contrast,
                        isChecked = isHighContrastModeEnabled, onClick = {
                            PreferencesUtil.modifyDarkThemePreference(isHighContrastModeEnabled = !isHighContrastModeEnabled)
                        }
                    )
                }
            }
        })
}