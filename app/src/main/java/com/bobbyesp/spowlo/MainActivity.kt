package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.bobbyesp.miniplayer_service.service.SpowloMediaService
import com.bobbyesp.spowlo.features.spotifyApi.data.local.notifications.SpotifyBroadcastReceiver
import com.bobbyesp.spowlo.features.spotifyApi.utils.notifications.registerSpBroadcastReceiver
import com.bobbyesp.spowlo.ui.Navigator
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.pages.LoginManagerViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.ON_WRITE_DATA_REQUEST_CODE
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isMusicPlayerServiceOn = false

    val loginVmManager by viewModels<LoginManagerViewModel>()
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this
        spotifyBroadcastReceiver = SpotifyBroadcastReceiver()
        registerSpBroadcastReceiver(
            spotifyBroadcastReceiver,
            *SpotifyBroadcastType.entries.toTypedArray()
        )
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startMediaPlayerService()
                }
            }
            var corruptedCredentials by remember {
                mutableStateOf(intent.getBooleanExtra("spotifyCredsCrash", false))
            }
            val windowSizeClass = calculateWindowSizeClass(this)
            AppLocalSettingsProvider(windowSizeClass.widthSizeClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigator(loginVmManager)

                    if (corruptedCredentials) {
                        AlertDialog(
                            onDismissRequest = { corruptedCredentials = false },
                            confirmButton = { /*TODO*/ },
                            text = {
                                Text(text = stringResource(id = R.string.spotify_credentials_corrupted_desc))
                            },
                            title = {
                                Text(text = stringResource(id = R.string.spotify_credentials_corrupted))
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, SpowloMediaService::class.java))
        isMusicPlayerServiceOn = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ON_WRITE_DATA_REQUEST_CODE) {
            //Do something
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startMediaPlayerService() {
        if (!isMusicPlayerServiceOn) {
            startForegroundService(Intent(this, SpowloMediaService::class.java))
            isMusicPlayerServiceOn = true
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var activity: MainActivity
        lateinit var spotifyBroadcastReceiver: SpotifyBroadcastReceiver
        fun getActivity(): MainActivity {
            return activity
        }
    }
}