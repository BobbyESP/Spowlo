package com.bobbyesp.spowlo.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R
import com.bobbyesp.utilities.utilities.URL.URI.UTF8
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URLEncoder

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

    data object OptionsDialog : Route("options_dialog", title = R.string.options_dialog)

    data object Search: Route(
        "search/{query}",
        title = R.string.search,
        icon = Icons.Rounded.Search,
    ) {
        /**
         * Creates a route with the given query. In this case it's not necessary to encode the query as a JSON.
         * @param query The query to search for.
         * @return The route with the given query.
         */
        fun createRoute(query: String): String {
            return "search/${URLEncoder.encode(query, UTF8)}"
        }
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

/**
 * The arguments that can be passed to routes.
 */
enum class NavArgs(val key: String) {
    SearchQuery(key = "query"),
}