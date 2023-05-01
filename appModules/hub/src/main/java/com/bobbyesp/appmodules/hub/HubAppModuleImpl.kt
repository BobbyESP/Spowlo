package com.bobbyesp.appmodules.hub

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.twotone.Home
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.bobbyesp.appmodules.core.Destinations
import com.bobbyesp.appmodules.core.NavigationEntry
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.core.navigation.ext.ROOT_NAV_GRAPH_ID
import com.bobbyesp.appmodules.core.utils.Log
import com.bobbyesp.appmodules.hub.ui.dac.DacRendererPage
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import javax.inject.Inject

class HubAppModuleImpl @Inject constructor() : HubAppModule() {

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun NavGraphBuilder.buildGraph(
        navController: NavHostController,
        destinations: Destinations,
    ) {
        composable(Routes.DacRenderer.url) {
            DacRendererPage(
                title = "",
                loader = {
                    Log.d("DAC", "Loading home")
                    val homeResponse = getDacHome(SpotifyInternalApi.buildDacRequestForHome(it))
                    Log.d("DAC", homeResponse.toString())
                    homeResponse
                },
                onGoBack = { navController.popBackStack() },
                fullscreen = true
            )
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