package com.bobbyesp.spowlo.ui.common

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.ui.navigation.LocalNavigationController
import com.bobbyesp.spowlo.ui.navigation.NavigationController
import com.bobbyesp.spowlo.ui.theme.DEFAULT_SEED_COLOR
import com.bobbyesp.spowlo.utils.DarkThemePreference
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.bobbyesp.spowlo.utils.palettesMap
import com.bobbyesp.uisdk.LocalFloatingBarInset
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
val LocalWindowHeightState = staticCompositionLocalOf { WindowHeightSizeClass.Compact }
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalPaletteStyleIndex = compositionLocalOf { 0 }

@Composable
fun SettingsProvider(
    windowWidthSizeClass: WindowWidthSizeClass,
    localWindowHeightSizeClass: WindowHeightSizeClass,
    navBarInsets: Dp = 0.dp,
    navController : NavHostController,
    content: @Composable () -> Unit) {
    val appSettingsState = PreferencesUtil.AppSettingsStateFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides appSettingsState.darkTheme,
        LocalSeedColor provides appSettingsState.seedColor,
        LocalPaletteStyleIndex provides appSettingsState.paletteStyleIndex,
        LocalTonalPalettes provides Color(appSettingsState.seedColor).toTonalPalettes(
            palettesMap.getOrElse(appSettingsState.paletteStyleIndex) { PaletteStyle.TonalSpot }
        ),
        LocalWindowWidthState provides windowWidthSizeClass,
        LocalWindowHeightState provides localWindowHeightSizeClass,
        LocalDynamicColorSwitch provides appSettingsState.isDynamicColorEnabled,
        LocalNavigationController provides NavigationController { navController },
        LocalFloatingBarInset provides navBarInsets,
        content = content
    )
}