package com.bobbyesp.spowlo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spowlo.presentation.Navigator
import com.bobbyesp.spowlo.presentation.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.presentation.common.LocalDarkTheme
import com.bobbyesp.spowlo.presentation.theme.SpowloTheme
import com.zionhuang.innertube.models.SongItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this
        enableEdgeToEdge()
        setContent {
            val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
            AppLocalSettingsProvider(windowWidthClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigator(
                        handledIntent = intent,
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var activity: MainActivity
        fun getActivity(): MainActivity {
            return activity
        }

        const val ACTION_SEARCH = "${App.APP_PACKAGE_NAME}.action.SEARCH"
        const val ACTION_SONGS = "${App.APP_PACKAGE_NAME}.action.SONGS"
        const val ACTION_ALBUMS = "${App.APP_PACKAGE_NAME}.action.ALBUMS"
        const val ACTION_PLAYLISTS = "${App.APP_PACKAGE_NAME}.action.PLAYLISTS"

        var sharedSong: MutableState<SongItem?> = mutableStateOf(null)
    }
}