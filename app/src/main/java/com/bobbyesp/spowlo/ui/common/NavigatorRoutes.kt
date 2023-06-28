package com.bobbyesp.spowlo.ui.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalPlay
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.App
import com.bobbyesp.spowlo.R

sealed class Route(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
) {
    //NAVIGATION HOSTS
    object MainHost : Route("main_host", getStringWithContext(R.string.app_name))

    //NAVIGATORS
    object HomeNavigator : Route("home_navigator", getStringWithContext(R.string.home), Icons.Outlined.Home)
    object UtilitiesNavigator : Route("utilities_navigator", getStringWithContext(R.string.utilities), Icons.Outlined.LocalPlay)


    //ROUTES
    object Home : Route("home", getStringWithContext(R.string.home), Icons.Outlined.Home)
    object Utilities : Route("utilities", getStringWithContext(R.string.utilities), Icons.Outlined.LocalPlay)
    object LyricsDownloaderPage : Route("lyrics_downloader_page", getStringWithContext(R.string.lyrics_downloader), Icons.Default.Lyrics)
    object OnboardingPage : Route("onboarding_page", getStringWithContext(R.string.onboarding))

}

fun getStringWithContext(
    @StringRes resId: Int,
): String {
    return App.context.getString(resId)
}
