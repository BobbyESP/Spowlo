package com.bobbyesp.appmodules.hub

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.bobbyesp.appmodules.core.BottomNavigationCapable
import com.bobbyesp.appmodules.core.DestNode
import com.bobbyesp.appmodules.core.HasFullscreenRoutes
import com.bobbyesp.appmodules.core.NestedAppEntry

abstract class HubAppModule: NestedAppEntry, HasFullscreenRoutes, BottomNavigationCapable {
    override val graphRoute = Routes.NavGraph
    override val startDestination = Routes.DacRenderer.url

    val deeplinkCapable = mapOf(Routes.SpotifyCapableUri to "https://open.spotify.com/{type}/{typeId}")

    override val fullscreenRoutes = listOf(
        Routes.SpotifyCapableUri.url,
    )
    internal object Arguments {
        val SongUri = navArgument("uri") {
            type = NavType.StringType
        }
    }

    internal object Routes {
        const val NavGraph = "@hub"

        val DacRenderer = DestNode("hub/dacRenderer")
        val SpotifyCapableUri = DestNode("spotify:{${Arguments.SongUri.name}}")

    }
}