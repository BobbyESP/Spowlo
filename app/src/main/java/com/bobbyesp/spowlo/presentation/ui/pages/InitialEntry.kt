package com.bobbyesp.spowlo.presentation.ui.pages

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bobbyesp.spowlo.presentation.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.presentation.ui.common.Route
import com.bobbyesp.spowlo.presentation.ui.common.animatedComposable
import com.bobbyesp.spowlo.presentation.ui.pages.home.HomePage
import com.bobbyesp.spowlo.presentation.ui.pages.settings.SettingsPage
import com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence.AppearancePreferences
import com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence.DarkThemePreferences
import com.bobbyesp.spowlo.presentation.ui.pages.settings.appearence.LanguagesPreferences
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

private const val TAG = "InitialEntry"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InitialEntry() {
    val navController = rememberAnimatedNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val onBackPressed = { navController.popBackStack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ){
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxWidth(
                    when (LocalWindowWidthState.current) {
                        WindowWidthSizeClass.Compact -> 1f
                        WindowWidthSizeClass.Expanded -> 0.5f
                        else -> 0.8f
                    }
                )
                .align(Alignment.Center),
            navController = navController,
            startDestination = Route.HOME){

            animatedComposable(Route.HOME){
                HomePage(navController = navController)
            }

            animatedComposable(Route.SETTINGS){
                SettingsPage(navController)
            }

            animatedComposable(Route.ABOUT){
                //TODO Add About Page
            }

            animatedComposable(Route.DISPLAY_SETTINGS){
                AppearancePreferences(navController)
            }

            animatedComposable(Route.LANGUAGES){
                LanguagesPreferences{
                    onBackPressed()
                }
            }

            animatedComposable(Route.DARK_THEME_SELECTOR){
                DarkThemePreferences {
                    onBackPressed()
                }
            }

        }
    }
}