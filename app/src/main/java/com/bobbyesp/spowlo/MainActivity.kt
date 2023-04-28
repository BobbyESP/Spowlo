package com.bobbyesp.spowlo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.bobbyesp.spowlo.App.Companion.context
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme
import com.bobbyesp.spowlo.ui.common.LocalDynamicColorSwitch
import com.bobbyesp.spowlo.ui.common.SettingsProvider
import com.bobbyesp.spowlo.ui.pages.InitialEntry
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.utils.PreferencesUtil
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var provider: (() -> NavController)? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterialApi::class,
        ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class
    )
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
            val bottomSheetState = rememberBottomSheetScaffoldState()
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberAnimatedNavController(bottomSheetNavigator)
            val windowSizeClass = calculateWindowSizeClass(this)
            SettingsProvider(windowSizeClass.widthSizeClass, windowSizeClass.heightSizeClass, navController) {
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    InitialEntry(
                        navController = navController,
                        bottomSheetNavigator = bottomSheetNavigator,
                    )
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

        fun setLanguage(locale: String) {
            Log.d(TAG, "setLanguage: $locale")
            val localeListCompat = if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(locale)
            App.applicationScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(localeListCompat)
            }
        }
    }
}