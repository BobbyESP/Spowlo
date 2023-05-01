package com.bobbyesp.appModules.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.bobbyesp.appModules.auth.ui.auth.AuthDisclaimerBottomSheet
import com.bobbyesp.appModules.auth.ui.auth.AuthScreen
import com.bobbyesp.appModules.auth.ui.auth.OnboardPage
import com.bobbyesp.appmodules.core.Destinations
import com.bobbyesp.appmodules.core.find
import com.bobbyesp.appmodules.core.navigation.ext.navigateRoot
import com.bobbyesp.appmodules.hub.HubAppModule
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import javax.inject.Inject

class AuthAppModuleImpl @Inject constructor() : AuthAppModule() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun NavGraphBuilder.buildGraph(
        navController: NavHostController,
        destinations: Destinations
    ) {
        composable(Routes.MainScreen.url) {
            OnboardPage(
                onAuthClicked = {
                    navController.navigate(Routes.SignInScreen.url)
                },
                onOnlyDownloaderClicked = {
                    //navController.navigateRoot(destinations.find<DownloaderAppModule>().graphRoute)
                }
            )
        }
        composable(Routes.SignInScreen.url) {
            AuthScreen(
                onShowDisclaimer = {
                    navController.navigate(Routes.AuthDisclaimer.url)
                },
                onProceedToNextStep = {
                    navController.navigateRoot(destinations.find<HubAppModule>().graphRoute)
                },
                onBackClicked = {
                    navController.popBackStack()
                })
        }
        bottomSheet(Routes.AuthDisclaimer.url) {
            AuthDisclaimerBottomSheet(
                onCancel = {
                    navController.popBackStack()
                },
            )
        }
    }
}