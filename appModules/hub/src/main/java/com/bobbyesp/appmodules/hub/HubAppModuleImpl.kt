package com.bobbyesp.appmodules.hub

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.twotone.Home
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import com.bobbyesp.appmodules.core.Destinations
import com.bobbyesp.appmodules.core.NavigationEntry
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.navigation.ext.ROOT_NAV_GRAPH_ID
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.appmodules.hub.ui.HubNavigationController
import com.bobbyesp.appmodules.hub.ui.LocalHubNavigationController
import com.bobbyesp.appmodules.hub.ui.dac.DacRendererPage
import com.bobbyesp.appmodules.hub.ui.screens.dynamic.DynamicSpotifyUriScreen
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import javax.inject.Inject

class HubAppModuleImpl @Inject constructor() : HubAppModule() {

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun NavGraphBuilder.buildGraph(
        navController: NavHostController,
        destinations: Destinations,
    ) {
        val lambdaNavController = HubNavigationController { navController }
        composable(Routes.DacRenderer.url) {
            DacRendererPage(
                title = "",
                loader = { bFacet ->
                    Log.d("DAC", "Loading home...")
                    val homeResponse = getDacHome(SpotifyInternalApi.buildDacRequestForHome(bFacet))
                    homeResponse
                },
                onGoBack = { navController.popBackStack() },
                fullscreen = true,
                onNavigateToRequested = {
                    Log.d("DAC", "Navigating to $it")
                    navController.navigate(it)
                },
            )
        }
        composable(Routes.SpotifyCapableUri.url, deepLinks = listOf(
            navDeepLink {
                uriPattern = deeplinkCapable.getValue(Routes.SpotifyCapableUri)
                action = Intent.ACTION_VIEW
            }
        )) {
            val fullUrl = it.arguments?.getString("uri")
            val dpLinkType = it.arguments?.getString("type")
            val dpLinkTypeId = it.arguments?.getString("typeId")
            val uri = fullUrl ?: "$dpLinkType:$dpLinkTypeId"
            CompositionLocalProvider(LocalHubNavigationController provides lambdaNavController) {
                DynamicSpotifyUriScreen(
                    uri,
                    "spotify:$uri",
                    onBackPressed = { navController.popBackStack() })
            }
        }
    }

    private fun NavHostController.navigateToRoot() {
        navigate(Routes.DacRenderer.url) {
            popUpTo(ROOT_NAV_GRAPH_ID)
        }
    }

    override val bottomNavigationEntry = NavigationEntry(
        route = Routes.NavGraph,
        name = R.string.home,
        icon = { Icons.TwoTone.Home },
        iconSelected = { Icons.Filled.Home },
    )
}