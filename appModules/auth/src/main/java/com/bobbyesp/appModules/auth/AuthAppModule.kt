package com.bobbyesp.appModules.auth

import com.bobbyesp.appmodules.core.DestNode
import com.bobbyesp.appmodules.core.HasFullscreenRoutes
import com.bobbyesp.appmodules.core.NestedAppEntry

abstract class AuthAppModule: NestedAppEntry, HasFullscreenRoutes {
    override val graphRoute = Routes.NavGraph
    override val startDestination = Routes.MainScreen.url

    override val fullscreenRoutes = listOf(
        Routes.MainScreen.url,
        Routes.SignInScreen.url,
    )
    internal object Arguments {
    }

    internal object Routes {
        const val NavGraph = "@auth"

        val MainScreen = DestNode("auth/onboarding")

        val SignInScreen = DestNode("auth/username")

        val AuthDisclaimer = DestNode("auth/disclaimer")
    }
}