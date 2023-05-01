package com.bobbyesp.appmodules.hub

import com.bobbyesp.appmodules.core.DestNode
import com.bobbyesp.appmodules.core.HasFullscreenRoutes
import com.bobbyesp.appmodules.core.NestedAppEntry

abstract class HubAppModule: NestedAppEntry, HasFullscreenRoutes {
    override val graphRoute = Routes.NavGraph
    override val startDestination = Routes.DacRenderer.url

    override val fullscreenRoutes = listOf(
        Routes.DacRenderer.url,
    )
    internal object Arguments {
    }

    internal object Routes {
        const val NavGraph = "@hub"

        val DacRenderer = DestNode("hub/dacRenderer")
    }
}