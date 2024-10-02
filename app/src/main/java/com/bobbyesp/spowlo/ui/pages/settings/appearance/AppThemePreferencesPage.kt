package com.bobbyesp.spowlo.ui.pages.settings.appearance

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.components.BackButton
import com.bobbyesp.spowlo.ui.components.PreferenceSubtitle
import com.bobbyesp.spowlo.ui.components.settings.SettingsNewSingleChoiceItem
import com.bobbyesp.spowlo.ui.components.settings.SettingsSwitch
import com.bobbyesp.spowlo.utils.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.bobbyesp.spowlo.utils.DarkThemePreference.Companion.OFF
import com.bobbyesp.spowlo.utils.DarkThemePreference.Companion.ON
import com.bobbyesp.spowlo.utils.PreferencesUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppThemePreferencesPage(onBackPressed: () -> Unit) {
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
                        text = stringResource(R.string.dark_theme), fontWeight = FontWeight.Bold
                    )
                }, navigationIcon = {
                    BackButton() {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
            ) {
                item {
                    SettingsNewSingleChoiceItem(
                        text = stringResource(R.string.follow_system),
                        selected = darkThemePreference.darkThemeValue == FOLLOW_SYSTEM,
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp
                            )
                        )
                    ) { PreferencesUtil.modifyDarkThemePreference(FOLLOW_SYSTEM) }
                }

                item {
                    SettingsNewSingleChoiceItem(
                        text = stringResource(R.string.on),
                        selected = darkThemePreference.darkThemeValue == ON
                    ) { PreferencesUtil.modifyDarkThemePreference(ON) }
                }

                item {
                    SettingsNewSingleChoiceItem(
                        text = stringResource(R.string.off),
                        selected = darkThemePreference.darkThemeValue == OFF,
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        )
                    ) { PreferencesUtil.modifyDarkThemePreference(OFF) }
                }
                item {
                    PreferenceSubtitle(text = stringResource(R.string.additional_settings))
                }
                item {
                    SettingsSwitch(
                        onCheckedChange = {
                            PreferencesUtil.modifyDarkThemePreference(isHighContrastModeEnabled = !isHighContrastModeEnabled)
                        },
                        checked = isHighContrastModeEnabled,
                        title = { Text(text = stringResource(R.string.high_contrast)) },
                        icon = Icons.Outlined.Contrast,
                        clipCorners = false,
                        modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 8.dp, bottomEnd = 8.dp,
                                topStart = 8.dp, topEnd = 8.dp
                            )
                        ),
                    )
                }
            }
        })
}