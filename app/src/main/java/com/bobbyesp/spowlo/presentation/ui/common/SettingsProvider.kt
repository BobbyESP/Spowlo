package com.bobbyesp.spowlo.presentation.ui.common

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import com.bobbyesp.spowlo.presentation.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.bobbyesp.spowlo.util.PreferencesUtil

val LocalDarkTheme = compositionLocalOf { PreferencesUtil.DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
val settingFlow = PreferencesUtil.AppSettingsStateFlow
val LocalDynamicColorSwitch = compositionLocalOf { false }

@Composable
fun SettingsProvider(windowWidthSizeClass: WindowWidthSizeClass, content: @Composable () -> Unit) {
    val appSettingsState = settingFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides appSettingsState.darkTheme,
        LocalSeedColor provides appSettingsState.seedColor,
        LocalWindowWidthState provides windowWidthSizeClass,
        LocalDynamicColorSwitch provides appSettingsState.isDynamicColorEnabled,
        content = content
    )
}
