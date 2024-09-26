package com.bobbyesp.spowlo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.bobbyesp.spowlo.features.spotify.data.local.broadcast.SpotifyBroadcastObserverImpl
import com.bobbyesp.spowlo.features.spotify.data.local.broadcast.registerSpotifyBroadcastReceiver
import com.bobbyesp.spowlo.presentation.Navigator
import com.bobbyesp.spowlo.presentation.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.presentation.common.LocalDarkTheme
import com.bobbyesp.spowlo.presentation.pages.auth.SpotifyAuthManagerViewModel
import com.bobbyesp.spowlo.presentation.theme.SpowloTheme

class MainActivity : ComponentActivity() {
    val authManagerViewModel: SpotifyAuthManagerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this
        spotifyBroadcastManager = SpotifyBroadcastObserverImpl()
        registerSpotifyBroadcastReceiver(
            spotifyBroadcastManager,
            *SpotifyBroadcastType.entries.toTypedArray()
        )
        enableEdgeToEdge()
        setContent {
            val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
            AppLocalSettingsProvider(windowWidthClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigator(authManagerViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(spotifyBroadcastManager)
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var activity: MainActivity
        fun getActivity(): MainActivity {
            return activity
        }
        lateinit var spotifyBroadcastManager: SpotifyBroadcastObserverImpl

        const val ACTION_SEARCH = "${App.APP_PACKAGE_NAME}.action.SEARCH"
    }
}