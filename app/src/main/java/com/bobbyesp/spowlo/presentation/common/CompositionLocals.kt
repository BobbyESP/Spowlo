package com.bobbyesp.spowlo.presentation.common

import android.os.Build
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.bobbyesp.spowlo.features.notification_manager.data.local.NotificationManagerImpl
import com.bobbyesp.spowlo.features.notification_manager.domain.NotificationManager
import com.bobbyesp.utilities.DarkThemePreference
import com.bobbyesp.utilities.Theme.paletteStyles
import com.bobbyesp.utilities.preferences.Preferences.AppSettingsStateFlow
import com.bobbyesp.utilities.ui.DEFAULT_SEED_COLOR
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.skydoves.landscapist.coil.LocalCoilImageLoader
import kotlinx.coroutines.Dispatchers

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor =
    compositionLocalOf { DEFAULT_SEED_COLOR } //This is the color in which the app will be based on for creating the palette
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalIndexOfPaletteStyle = compositionLocalOf { 0 }
val LocalWindowWidthState =
    staticCompositionLocalOf { WindowWidthSizeClass.COMPACT } //This value probably will never change, that's why it is static
val LocalOrientation = compositionLocalOf<Int> { error("No orientation provided") }
val LocalNavController =
    compositionLocalOf<NavHostController> { error("No nav controller provided") }
val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No snackbar host state provided") }
val LocalNotificationManager =
    staticCompositionLocalOf<NotificationManager> { error("No notifications manager provided") }
@Composable
fun AppLocalSettingsProvider(
    windowWidthSize: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val appSettingsState = AppSettingsStateFlow.collectAsStateWithLifecycle().value
    val navController = rememberNavController()
    val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.35)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(7 * 1024 * 1024)
                .build()
        }
        .respectCacheHeaders(false)
        .allowHardware(true)
        .crossfade(true)
        .bitmapFactoryMaxParallelism(8)
        .dispatcher(Dispatchers.IO)
        .build()
    val config = LocalConfiguration.current
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
            LocalOrientation provides config.orientation,
            LocalSnackbarHostState provides snackbarHostState,
            LocalNotificationManager provides NotificationManagerImpl
        ) {
            content() //The content of the app
        }
    }
}