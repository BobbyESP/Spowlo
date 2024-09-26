package com.bobbyesp.spowlo.presentation.common

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.util.fastFirstOrNull
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object MainHost : Route

    @Serializable
    data object Dialogs : Route {
        @Serializable
        data object MainSettings : Route
    }

    @Serializable
    data object Spotify : Route {

        @Serializable
        data object Auth : Route

        @Serializable
        data object HomeNavigator : Route {
            @Serializable
            data object Home : Route
        }

        @Serializable
        data object SearchNavigator : Route {

            @Serializable
            data class Search(val query: String) : Route
        }

        @Serializable
        data object ProfileNavigator : Route {
            @Serializable
            data object Profile : Route
        }
    }
}

val providers = listOf(
    Route.Spotify,
)

val providerRoutes = mapOf(
    Route.Spotify to listOf(
        Route.Spotify.HomeNavigator,
        Route.Spotify.SearchNavigator,
        Route.Spotify.ProfileNavigator
    )
)

fun mainRoutesForProvider(provider: Route): List<Route> {
    return providerRoutes[provider] ?: emptyList()
}

fun childRoutesForProvider(provider: Any): List<Route> {
    return when (provider) {
        is Route -> providerRoutes[provider]
        is String -> providerRoutes[providers.first { it::class.qualifiedName == provider }]
        else -> null
    } ?: emptyList()
}

fun String.asProviderRoute(): Route {
    return providers.fastFirstOrNull { it::class.qualifiedName == this } ?: Route.Spotify
}

fun Any.isHomeRoute(): Boolean {
    return when (this) {
        is Route -> this is Route.Spotify.HomeNavigator.Home
        is String -> this == Route.Spotify.HomeNavigator.Home::class.qualifiedName
        else -> false
    }
}

val routeSaver: Saver<Route, String> = Saver(
    save = { route -> route::class.qualifiedName.toString() },
    restore = { it.asProviderRoute() }
)