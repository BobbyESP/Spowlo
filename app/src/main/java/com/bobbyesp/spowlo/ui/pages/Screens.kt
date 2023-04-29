package com.bobbyesp.spowlo.ui.pages

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R
import javax.annotation.concurrent.Immutable

@Immutable
enum class Screen(
    val route: String,
    val icon: ImageVector? = null,
    @StringRes val title: Int = 0,
) {
    //Internal routes
    NavGraph("nav_graph"),
    CoreLoading("coreLoading"),
    Authorization("auth"),
    SpotifyDeeplinkRedirect("spotify:{uri}"),

    //Navigators
    AuthNavigator("authNavigator"),
    HomeNavigator("homeNavigator"),
    SearchNavigator("searchNavigator"),
    LibraryNavigator("libraryNavigator"),

    //Bottom Navigation routes
    Home("feed", title = R.string.tab_home),
    Search("search", title = R.string.tab_search),
    Library("library", title = R.string.tab_library);
    companion object {
        val hideNavigationBar = setOf(CoreLoading.route, Authorization.route, Dialog.AuthDisclaimer.route)
        val deeplinkCapable = mapOf(SpotifyDeeplinkRedirect to "https://open.spotify.com/{type}/{typeId}")
        val showInBottomNavigation = mapOf(
            Home to Icons.Rounded.Home,
            Search to Icons.Rounded.Search,
            Library to Icons.Rounded.LibraryMusic
        )
    }
}

@Immutable
enum class Dialog(
    val route: String
) {
    AuthDisclaimer("dialogs/disclaimers")
}

@Immutable
enum class BottomSheet(
    val route: String
) { }