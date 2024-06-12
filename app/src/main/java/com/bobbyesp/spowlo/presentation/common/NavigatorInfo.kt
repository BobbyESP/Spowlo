package com.bobbyesp.spowlo.presentation.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.bobbyesp.spowlo.R

enum class NavigatorInfo(
    val icon: ImageVector,
    @StringRes val title: Int
) {
    HOME(
        icon = Icons.Rounded.Home,
        title = R.string.home
    ),
    SEARCH(
        icon = Icons.Rounded.Search,
        title = R.string.search
    ),
    PROFILE(
        icon = Icons.Rounded.Person,
        title = R.string.profile
    );

    companion object {
        fun fromRoute(route: Route): NavigatorInfo? {
            return when (route) {
                is Route.YoutubeMusic.HomeNavigator, Route.Spotify.HomeNavigator -> HOME
                is Route.YoutubeMusic.SearchNavigator, Route.Spotify.SearchNavigator -> SEARCH
                is Route.Spotify.ProfileNavigator -> PROFILE
                else -> null
            }
        }
    }
}