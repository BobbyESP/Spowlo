package com.bobbyesp.spowlo.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bobbyesp.spowlo.Spowlo
import com.bobbyesp.spowlo.Spowlo.Companion.applicationScope
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.presentation.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.presentation.ui.common.LocalDynamicColorSwitch
import com.bobbyesp.spowlo.presentation.ui.common.LocalSeedColor
import com.bobbyesp.spowlo.presentation.ui.common.SettingsProvider
import com.bobbyesp.spowlo.presentation.ui.pages.InitialEntry
import com.bobbyesp.spowlo.presentation.ui.pages.home.HomeViewModel
import com.bobbyesp.spowlo.presentation.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.util.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  //  private val homeViewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
       runBlocking {
            if (Build.VERSION.SDK_INT < 33)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(PreferencesUtil.getLanguageConfiguration())
                )
        }
        context = this.baseContext
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            SettingsProvider(windowSizeClass.widthSizeClass){
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    seedColor = LocalSeedColor.current,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    val viewModel = hiltViewModel<HomeViewModel>()
                    InitialEntry(viewModel)
                }
            }

        }
    }

    companion object {
        private const val TAG = "MainActivity"

        fun setLanguage(locale: String) {
            Log.d(TAG, "setLanguage: $locale")
            Log.d(TAG, "LOCALE EMPTY?: ${locale.isEmpty()}")
            val localeListCompat =
                if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(locale)
            applicationScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(localeListCompat)
            }
        }
    }
}