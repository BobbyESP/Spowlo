package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.bobbyesp.miniplayer_service.service.SpowloMediaService
import com.bobbyesp.spowlo.ui.Navigator
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isMusicPlayerServiceOn = false

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this

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
                    Navigator()
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
        fun getActivity(): MainActivity {
            return activity
        }
    }
}