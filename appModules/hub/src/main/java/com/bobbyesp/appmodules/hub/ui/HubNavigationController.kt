package com.bobbyesp.appmodules.hub.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

@JvmInline
@Immutable
value class HubNavigationController(
    val controller: () -> NavHostController
) {
    fun navigate(route: String) = controller().navigate(route)
    fun popBackStack() = controller().popBackStack()
    fun context() = controller().context
}

val LocalHubNavigationController = staticCompositionLocalOf<HubNavigationController> { error("HubNavigationController should be initialized") }