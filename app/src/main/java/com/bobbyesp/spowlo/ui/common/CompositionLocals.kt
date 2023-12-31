package com.bobbyesp.spowlo.ui.common

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.bobbyesp.spowlo.features.notification_manager.data.local.NotificationManager
import com.bobbyesp.spowlo.features.notification_manager.data.local.NotificationManagerImpl
import com.bobbyesp.ui.components.bottomsheets.StaticBottomSheetState
import com.bobbyesp.utilities.utilities.DarkThemePreference
import com.bobbyesp.utilities.utilities.Theme.paletteStyles
import com.bobbyesp.utilities.utilities.preferences.Preferences.AppSettingsStateFlow
import com.bobbyesp.utilities.utilities.ui.DEFAULT_SEED_COLOR
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.skydoves.landscapist.coil.LocalCoilImageLoader


val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR } //This is the color in which the app will be based on for creating the palette
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalIndexOfPaletteStyle = compositionLocalOf { 0 }
val LocalWindowWidthState =
    staticCompositionLocalOf { WindowWidthSizeClass.Compact } //This value probably will never change, that's why it is static
val LocalNavController =
    compositionLocalOf<NavHostController> { error("No nav controller provided") }
val LocalPlayerInsetsAware =
    compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }
val LocalNotificationManager =
    compositionLocalOf<NotificationManager> { error("No notification manager instance provided") }
val LocalStaticBottomSheetState = compositionLocalOf<StaticBottomSheetState> { error("No static bottom sheet state provided") }
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No snackbar host state provided") }

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun AppLocalSettingsProvider(
    windowWidthSize: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val appSettingsState = AppSettingsStateFlow.collectAsState().value

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    val imageLoader = ImageLoader.Builder(context).build()

    val notificationManager by lazy { NotificationManagerImpl() }
    val staticBottomSheetState by lazy { StaticBottomSheetState() }
    val snackbarHostState = remember { SnackbarHostState() }

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
            LocalCoilImageLoader provides imageLoader,
            LocalNotificationManager provides notificationManager,
            LocalStaticBottomSheetState provides staticBottomSheetState,
            LocalSnackbarHostState provides snackbarHostState,
        ) {
            content() //The content of the app
        }
    }
}