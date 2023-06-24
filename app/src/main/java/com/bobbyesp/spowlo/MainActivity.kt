package com.bobbyesp.spowlo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.ui.common.AppLocalSettingsProvider
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

        context = this.baseContext
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            AppLocalSettingsProvider(windowSizeClass.widthSizeClass) {
                SpowloTheme {
                    Button(onClick = {
                        error("Crash!")
                    }) {
                        Text(text = "Tap to crash")
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}