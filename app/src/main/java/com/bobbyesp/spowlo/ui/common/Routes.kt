package com.bobbyesp.spowlo.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R
import dagger.hilt.android.qualifiers.ApplicationContext

sealed class Route(
    val route: String,
    @StringRes val title: Int? = null,
    val icon: ImageVector? = null,
) {
    data object MainHost : Route("main_host")

    data object HomeNavigator : Route(
        "home_navigator",
        title = R.string.home,
        icon = Icons.Rounded.Home,
    ) {
        data object Home :
            Route("home", title = R.string.home, icon = Icons.Rounded.Home)
    }
}

fun Route.getTitle(@ApplicationContext context: Context): String? {
    return title?.let { context.getString(it) }
}

val routesToShowInBottomBar = listOf(
    Route.HomeNavigator,
)

val routesWhereToShowNavBar = listOf(
    Route.HomeNavigator.Home,
)
