package com.bobbyesp.appmodules.hub

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.bobbyesp.appmodules.core.Destinations
import com.bobbyesp.appmodules.core.api.interalApi.SpotifyInternalApi
import com.bobbyesp.appmodules.hub.ui.dac.DacRendererPage
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import javax.inject.Inject

class HubAppModuleImpl @Inject constructor() : HubAppModule() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun NavGraphBuilder.buildGraph(
        navController: NavHostController,
        destinations: Destinations
    ) {
        composable(Routes.DacRenderer.url) {
            DacRendererPage(title = "", loader = {
                getDacHome(SpotifyInternalApi.buildDacRequestForHome(it))
            }, onGoBack = { navController.popBackStack() })
        }
    }

}