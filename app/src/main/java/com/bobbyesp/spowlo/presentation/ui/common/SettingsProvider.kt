package com.bobbyesp.spowlo.presentation.ui.common

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.presentation.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.bobbyesp.spowlo.util.PreferencesUtil

val LocalDarkTheme = compositionLocalOf { PreferencesUtil.DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val LocalVideoThumbnailLoader = staticCompositionLocalOf {
    ImageLoader.Builder(context).build()
}
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
val settingFlow = PreferencesUtil.AppSettingsStateFlow
val LocalDynamicColorSwitch = compositionLocalOf { false }

@Composable
fun SettingsProvider(windowWidthSizeClass: WindowWidthSizeClass, content: @Composable () -> Unit) {
    val appSettingsState = settingFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides appSettingsState.darkTheme,
        LocalVideoThumbnailLoader provides ImageLoader.Builder(LocalContext.current)
            .build(),
        LocalSeedColor provides appSettingsState.seedColor,
        LocalWindowWidthState provides windowWidthSizeClass,
        LocalDynamicColorSwitch provides appSettingsState.isDynamicColorEnabled,
        content = content
    )
}
