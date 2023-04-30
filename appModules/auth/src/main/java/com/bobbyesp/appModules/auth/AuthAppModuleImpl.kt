package com.bobbyesp.appModules.auth

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.bobbyesp.appModules.auth.ui.auth.AuthScreen
import com.bobbyesp.appModules.auth.ui.auth.onboard.OnboardPage
import com.bobbyesp.appmodules.core.Destinations
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
                onShowDisclaimer = { },
                onProceedToNextStep = { },
                onBackClicked = {
                    navController.popBackStack()
                })
        }
        bottomSheet(Routes.AuthDisclaimer.url) {

        }
    }
}