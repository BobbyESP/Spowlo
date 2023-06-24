package com.bobbyesp.spowlo.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bobbyesp.spowlo.ui.common.Route
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

private const val TAG = "Navigator"
@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Navigator() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRootRoute = remember(navBackStackEntry) {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Route.OnboardingPage.route
        )
    }
}