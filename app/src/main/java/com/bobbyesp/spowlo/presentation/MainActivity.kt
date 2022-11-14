package com.bobbyesp.spowlo.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spowlo.Spowlo.Companion.applicationScope
import com.bobbyesp.spowlo.Spowlo.Companion.context
import com.bobbyesp.spowlo.presentation.ui.common.*
import com.bobbyesp.spowlo.presentation.ui.components.BottomNavBar.BottomNavBar
import com.bobbyesp.spowlo.presentation.ui.components.BottomNavBar.NavBarItem
import com.bobbyesp.spowlo.presentation.ui.pages.InitialEntry
import com.bobbyesp.spowlo.presentation.ui.pages.home.HomeViewModel
import com.bobbyesp.spowlo.presentation.ui.theme.SpowloTheme
import com.bobbyesp.spowlo.util.PreferencesUtil
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class,
        ExperimentalAnimationApi::class
    )
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
            val navController = rememberAnimatedNavController()
            val windowSizeClass = calculateWindowSizeClass(this)
            SettingsProvider(windowSizeClass.widthSizeClass){
                SpowloTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    seedColor = LocalSeedColor.current,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    Scaffold(
                        bottomBar = {
                            BottomNavBar(items = listOf(
                                NavBarItem(
                                    name = "Home",
                                    icon = Icons.Filled.Home,
                                    route = Route.HOME
                                ),
                                NavBarItem(
                                    name = "Settings",
                                    icon = Icons.Filled.Settings,
                                    route = Route.SETTINGS,
                                ),
                            ), navController = navController, onItemClicked = {
                                //if the current route is the same as the one we are trying to navigate to, do nothing
                                if (navController.currentDestination?.route != it.route) {
                                    navController.navigate(it.route)
                                }
                            })
                        }){

                        InitialEntry(homeViewModel, modifier = Modifier.padding(paddingValues = it), navController = navController)

                    }
                }
            }

        }
    }

    companion object {
        private const val TAG = "MainActivity"

        fun setLanguage(locale: String) {
            Log.d(TAG, "setLanguage: $locale")
            val localeListCompat =
                if (locale.isEmpty())
                {
                    LocaleListCompat.getEmptyLocaleList()
                }
                else {
                    LocaleListCompat.forLanguageTags(locale)
                }
            applicationScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(localeListCompat)
            }
        }
    }
}