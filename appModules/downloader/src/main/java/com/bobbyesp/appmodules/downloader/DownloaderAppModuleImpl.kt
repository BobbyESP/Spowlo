package com.bobbyesp.appmodules.downloader

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.twotone.Download
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.bobbyesp.appmodules.core.Destinations
import com.bobbyesp.appmodules.core.NavigationEntry
import com.bobbyesp.appmodules.core.navigation.ext.ROOT_NAV_GRAPH_ID
import com.bobbyesp.appmodules.downloader.ui.pages.DownloaderPage
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import javax.inject.Inject

class DownloaderAppModuleImpl @Inject constructor(): DownloaderAppModule() {

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun NavGraphBuilder.buildGraph(
        navController: NavHostController,
        destinations: Destinations,
    ) {
        composable(Routes.Downloader.url) {
            DownloaderPage(
                onGoBack = { navController.popBackStack() },
            )
        }
    }

    private fun NavHostController.navigateToRoot() {
        navigate(Routes.Downloader.url) {
            popUpTo(ROOT_NAV_GRAPH_ID)
        }
    }

    override val bottomNavigationEntry = NavigationEntry(
        route = Routes.NavGraph,
        name = R.string.downloader,
        icon = { Icons.TwoTone.Download },
        iconSelected = { Icons.Filled.Download },
    )
}