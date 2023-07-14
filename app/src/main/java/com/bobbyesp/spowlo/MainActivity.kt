package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.miniplayer_service.service.SpowloMediaService
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.login.SpotifyPkceLoginImpl
import com.bobbyesp.spowlo.ui.Navigator
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isServiceRunning = false

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this

        setContent {
//            LaunchedEffect(true) {
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startMediaPlayerService()
//                }
//            }
            val windowSizeClass = calculateWindowSizeClass(this)
            AppLocalSettingsProvider(windowSizeClass.widthSizeClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigator(
                        this
                    )
                }
            }
        }
    }
    override fun onDestroy() {

        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startMediaPlayerService() {
        if (!isServiceRunning) {
            startForegroundService(Intent(this, SpowloMediaService::class.java))
            isServiceRunning = true
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var activity: MainActivity
        fun getActivity(): MainActivity {
            return activity
        }
        fun startPkceLoginFlow() {
            activity.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
        }
    }
}