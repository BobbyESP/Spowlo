package com.bobbyesp.spowlo.ui.common

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bobbyesp.spowlo.ui.components.bottomsheets.BottomSheetMenuState
import com.bobbyesp.spowlo.ui.theme.DEFAULT_SEED_COLOR
import com.bobbyesp.spowlo.utils.preferences.PreferencesUtil.AppSettingsStateFlow
import com.bobbyesp.spowlo.utils.theme.DarkThemePreference
import com.bobbyesp.spowlo.utils.theme.ThemeUtil.paletteStyles
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalIndexOfPaletteStyle = compositionLocalOf { 0 }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact } //This value probably will never change, that's why it is static
val LocalNavController = compositionLocalOf<NavHostController> { error("No nav controller provided") }
val LocalBottomSheetMenuState = compositionLocalOf { BottomSheetMenuState() }
val LocalPlayerAwareWindowInsets = compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun AppLocalSettingsProvider(
    windowWidthSize: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    val appSettingsState = AppSettingsStateFlow.collectAsState().value
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    appSettingsState.run {
        CompositionLocalProvider(
            LocalDarkTheme provides darkTheme, //Tells the app if it should use dark theme or not
            LocalSeedColor provides seedColor, //Tells the app what color to use as seed for the palette
            LocalDynamicColorSwitch provides isDynamicColorEnabled, //Tells the app if it should use dynamic colors or not (Android 12+ feature)
            LocalIndexOfPaletteStyle provides paletteStyleIndex, //Tells the app what palette style to use depending on the index
            LocalWindowWidthState provides windowWidthSize, //Tells the app what is the current width of the window
            LocalNavController provides navController, //Tells the app what is the current nav controller
            LocalTonalPalettes provides if (isDynamicColorEnabled && Build.VERSION.SDK_INT >= 31) dynamicDarkColorScheme(
                LocalContext.current
            ).toTonalPalettes()
            else Color(seedColor).toTonalPalettes(
                paletteStyles.getOrElse(paletteStyleIndex) { PaletteStyle.TonalSpot }
            ), // Tells the app what is the current palette to use
            LocalBottomSheetMenuState provides BottomSheetMenuState()
        ) {
            content() //The content of the app
        }
    }
}