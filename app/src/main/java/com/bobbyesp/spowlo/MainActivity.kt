package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FileDownloadDone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.common.LocalDynamicColorSwitch
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.SettingsProvider
import com.bobbyesp.spowlo.ui.pages.InitialEntry
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.utils.PreferencesUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        runBlocking {
            if (Build.VERSION.SDK_INT < 33) AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(PreferencesUtil.getLanguageConfiguration())
            )
        }
        context = this.baseContext
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            SettingsProvider(windowSizeClass.widthSizeClass, windowSizeClass.heightSizeClass) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    InitialEntry()
                }
            }
        }
    }

    //This function is very important.
    //It handles the intent of opening the app from a shared link and put it in the url field
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
        private var sharedUrl = ""

        fun setLanguage(locale: String) {
            Log.d(TAG, "setLanguage: $locale")
            val localeListCompat = if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(locale)
            App.applicationScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(localeListCompat)
            }
        }

        val showInBottomNavigation = mapOf(
            Route.DownloaderNavi to Icons.Rounded.Download,
            Route.SearcherNavi to Icons.Rounded.Search,
            Route.DownloadTasksNavi to Icons.Rounded.FileDownloadDone,
        )
    }
}