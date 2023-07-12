package com.bobbyesp.spowlo.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalPlay
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R

sealed class Route(
    val route: String,
    var title: String,
    val icon: ImageVector? = null,
) {
    //NAVIGATION HOSTS
    object MainHost : Route("main_host", "")

    //NAVIGATORS
    object HomeNavigator : Route("home_navigator", "", Icons.Outlined.Home)
    object UtilitiesNavigator : Route("utilities_navigator", "", Icons.Outlined.LocalPlay)
    object ProfileNavigator : Route("profile_navigator", "", Icons.Default.Person)
    object SettingsNavigator : Route("settings_navigator", "", Icons.Default.Settings)

    //ROUTES
    object Home : Route("home", "", Icons.Outlined.Home)
    object Utilities : Route("utilities", "", Icons.Outlined.LocalPlay)
        object LyricsDownloaderPage : Route("lyrics_downloader_page", "", Icons.Default.Lyrics)
        object MiniplayerPage : Route("miniplayer_page", "", Icons.Default.MusicNote)

    object Profile : Route("profile", "", Icons.Default.Person)


    object OnboardingPage : Route("onboarding_page", "")
    object Settings : Route("settings_page", "", Icons.Default.Settings)

    class StringUtils(
        private val context: Context
    ) {
        fun getStringWithContext(@StringRes resId: Int): String {
            return context.getString(resId)
        }
    }

    companion object {
        private lateinit var applicationContext: Context

        fun initialize(applicationContext: Context) {
            this.applicationContext = applicationContext
            val stringUtils = StringUtils(applicationContext)

            MainHost.title = stringUtils.getStringWithContext(R.string.app_name)

            HomeNavigator.title = stringUtils.getStringWithContext(R.string.home)
            UtilitiesNavigator.title = stringUtils.getStringWithContext(R.string.utilities)
            SettingsNavigator.title = stringUtils.getStringWithContext(R.string.settings)

            Home.title = stringUtils.getStringWithContext(R.string.home)

            Utilities.title = stringUtils.getStringWithContext(R.string.utilities)
            LyricsDownloaderPage.title = stringUtils.getStringWithContext(R.string.lyrics_downloader)
            MiniplayerPage.title = stringUtils.getStringWithContext(R.string.miniplayer)

            OnboardingPage.title = stringUtils.getStringWithContext(R.string.onboarding)

            Settings.title = stringUtils.getStringWithContext(R.string.settings)

            ProfileNavigator.title = stringUtils.getStringWithContext(R.string.profile)
            Profile.title = stringUtils.getStringWithContext(R.string.profile)
        }
    }
}

