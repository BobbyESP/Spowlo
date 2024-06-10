package com.bobbyesp.spowlo.presentation.common

import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object MainHost : Route

    @Serializable
    data object OptionsDialog : Route


    @Serializable
    data object Spotify : Route {

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
    }

    @Serializable
    data object YoutubeMusic : Route {
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
    }
}

val providers = listOf(
    Route.Spotify,
    Route.YoutubeMusic,
)

val spotifyMainRoutes = listOf(
    Route.Spotify.HomeNavigator,
)

val youtubeMusicMainRoutes = listOf(
    Route.YoutubeMusic.HomeNavigator,
    Route.YoutubeMusic.SearchNavigator
)

val providerRoutes = mapOf(
    Route.Spotify to spotifyMainRoutes,
    Route.YoutubeMusic to youtubeMusicMainRoutes,
)

fun mainRoutesForProvider(provider: Route): List<Route> {
    return when (provider) {
        Route.Spotify -> spotifyMainRoutes
        Route.YoutubeMusic -> youtubeMusicMainRoutes
        else -> emptyList()
    }
}

fun childRoutesForProvider(provider: Route): List<Route> {
    return providerRoutes[provider] ?: emptyList()
}

fun childRoutesForProvider(qualifiedName: String): List<Route> {
    return providerRoutes[providers.first { it::class.qualifiedName == qualifiedName }] ?: emptyList()
}

fun String.asProviderRoute(): Route {
    return providers.first { it::class.qualifiedName == this }
}

val routeSaver: Saver<Route, String> = Saver(
    save = { route -> route::class.qualifiedName.toString() },
    restore = { it.asProviderRoute() }
)