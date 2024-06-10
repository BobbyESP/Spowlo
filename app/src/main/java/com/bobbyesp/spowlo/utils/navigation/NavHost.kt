package com.bobbyesp.spowlo.utils.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.spowlo.presentation.common.asProviderRoute

val NavHostController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun NavHostController.navigateBack() {
    if (canGoBack) {
        popBackStack()
    }
}

fun <T : Any> NavHostController.cleanNavigate(destination: T) = navigate(destination) {
    popUpTo(currentMusicProvider()) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

fun NavHostController.currentMusicProvider(): Route {
    val currentRoute = currentBackStackEntry?.destination?.parent?.parent?.route ?: Route.Spotify::class.qualifiedName!!
    return currentRoute.asProviderRoute()
}