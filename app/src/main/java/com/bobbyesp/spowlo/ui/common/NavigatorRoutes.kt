package com.bobbyesp.spowlo.ui.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
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


    //ROUTES
    object Home : Route("home", getStringWithContext(R.string.home), Icons.Outlined.Home)
    object OnboardingPage : Route("onboarding_page", getStringWithContext(R.string.onboarding))

}

fun getStringWithContext(
    @StringRes resId: Int,
): String {
    return App.context.getString(resId)
}
