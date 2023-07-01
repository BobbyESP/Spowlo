package com.bobbyesp.spowlo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spowlo.ui.Navigator
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            AppLocalSettingsProvider(windowSizeClass.widthSizeClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigator()
                }
            }
        }
    }


    companion object {

        private const val TAG = "MainActivity"
    }
}