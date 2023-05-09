package com.bobbyesp.spowlo.ui.pages

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bobbyesp.appModules.auth.AuthAppModule
import com.bobbyesp.appmodules.core.BottomNavigationCapable
import com.bobbyesp.appmodules.core.ComposableAppEntry
import com.bobbyesp.appmodules.core.Destinations
import com.bobbyesp.appmodules.core.HasFullscreenRoutes
import com.bobbyesp.appmodules.core.NavigationEntry
import com.bobbyesp.appmodules.core.NestedAppEntry
import com.bobbyesp.appmodules.core.SpotifyAuthManager
import com.bobbyesp.appmodules.core.SpotifySessionManager
import com.bobbyesp.appmodules.core.find
import com.bobbyesp.appmodules.core.navigation.ext.ROOT_NAV_GRAPH_ID
import com.bobbyesp.appmodules.core.navigation.ext.navigateRoot
import com.bobbyesp.appmodules.downloader.DownloaderAppModule
import com.bobbyesp.appmodules.hub.HubAppModule
import com.bobbyesp.spowlo.ui.common.SettingsProvider
import com.bobbyesp.uisdk.components.bottomBar.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance
import javax.inject.Inject

@OptIn(
    ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class
)
@Composable
fun AppNavigation(
    viewModel: AppNavigationViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass,
) {
    val emptyWindowInsets = WindowInsets(0.dp)
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRootRoute = remember(navBackStackEntry) {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: ROOT_NAV_GRAPH_ID
        )
    }

    LaunchedEffect(Unit) {
        if (navController.currentDestination?.route != "coreLoading") return@LaunchedEffect
        if (viewModel.isSignedIn()) return@LaunchedEffect else viewModel.loginWithStoredAuth()
        navController.navigateRoot(viewModel.awaitSignInAndReturnDestination())
    }

    val shouldHideNavigationBar = remember(navBackStackEntry) {
        viewModel.fullscreenDestinations.any {
            it == navBackStackEntry?.destination?.route
        }
    }
    val navBarHeightDp = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val navOffset by animateDpAsState(
        if (shouldHideNavigationBar) 80.dp + navBarHeightDp else 0.dp, label = ""
    )
    val navOffsetReverse by animateDpAsState(
        if (!shouldHideNavigationBar) 80.dp + navBarHeightDp else 0.dp, label = ""
    )
    val slideDistance = rememberSlideDistance()

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)
        ),
        scrimColor = MaterialTheme.colorScheme.scrim.copy(0.5f),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(topBar = {}, bottomBar = {
            fun navigateTo(dest: NavigationEntry) {
                navController.navigate(dest.route) {
                    popUpTo(ROOT_NAV_GRAPH_ID) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            }

            val bottomBarItems: List<BottomBarItem> = remember(viewModel.bottomNavDestinations) {
                viewModel.bottomNavDestinations.map { entry ->
                    BottomBarItem.Icon(icon = entry.icon,
                        description = entry.name,
                        id = entry.route,
                        onClick = {
                            navigateTo(entry)
                        })
                }
            }

            val selectedItemIndex = remember(bottomBarItems, currentRootRoute) {
                bottomBarItems.indexOfFirst { dest ->
                    currentRootRoute.value == dest.id
                }.coerceAtLeast(0)
            }
            AnimatedVisibility(
                visible = !shouldHideNavigationBar, enter = fadeIn(), exit = fadeOut()
            ) {
                FloatingBottomBar(expanded = false,
                    selectedItem = selectedItemIndex,
                    items = bottomBarItems,
                    modifier = Modifier.offset {
                        IntOffset(
                            0, navOffset.toPx().toInt()
                        )
                    },
                    expandedContent = {})
            }
        }, contentWindowInsets = emptyWindowInsets
        ) { paddingValues ->
            SettingsProvider(
                windowSizeClass.widthSizeClass,
                windowSizeClass.heightSizeClass,
                navOffsetReverse,
            ) {
                AnimatedNavHost(navController = navController,
                    route = ROOT_NAV_GRAPH_ID,
                    startDestination = "coreLoading",
                    modifier = Modifier.padding(top = (paddingValues.calculateTopPadding()).let {
                            if (it.value < 0f) {
                                0.dp
                            } else {
                                it
                            }
                        }),
                    enterTransition = {
                        if (initialState.destination.route == "coreLoading") {
                            EnterTransition.None
                        } else {
                            viewModel.buildAnimation(this) { forwardDirection ->
                                materialSharedAxisXIn(
                                    forward = forwardDirection, slideDistance = slideDistance
                                )
                            }
                        }
                    },
                    exitTransition = {
                        if (initialState.destination.route == "coreLoading") {
                            ExitTransition.None
                        } else {
                            viewModel.buildAnimation(this) { forwardDirection ->
                                materialSharedAxisXOut(
                                    forward = forwardDirection, slideDistance = slideDistance
                                )
                            }
                        }
                    },
                    popEnterTransition = {
                        materialSharedAxisXIn(forward = false, slideDistance = slideDistance)
                    },
                    popExitTransition = {
                        materialSharedAxisXOut(forward = false, slideDistance = slideDistance)
                    }) {
                    composable("coreLoading") {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    viewModel.destinations.forEach { (_, value) ->
                        when (value) {
                            is ComposableAppEntry -> with(value) {
                                composable(
                                    navController, viewModel.destinations
                                )
                            }

                            is NestedAppEntry -> with(value) {
                                navigation(
                                    navController, viewModel.destinations
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@HiltViewModel
@JvmSuppressWildcards
class AppNavigationViewModel @Inject constructor(
    val destinations: Destinations,
    private val spotifySessionManager: SpotifySessionManager,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {
    val fullscreenDestinations =
        (destinations.values.filterIsInstance<HasFullscreenRoutes>().map { it.fullscreenRoutes }
            .flatten() + "coreLoading").distinct()

    val bottomNavDestinations = listOf<BottomNavigationCapable>(
        destinations.find<HubAppModule>(),
        destinations.find<DownloaderAppModule>(),
    ).map(BottomNavigationCapable::bottomNavigationEntry)

    fun awaitSignInAndReturnDestination(): String {
        return if (isSignedIn()) {
            destinations.find<HubAppModule>().graphRoute
        } else {
                destinations.find<AuthAppModule>().graphRoute
            }
        }

    fun isSignedIn(): Boolean {
        return spotifySessionManager.isSessionValid()
    }

    suspend fun loginWithStoredAuth() {
        spotifyAuthManager.authStored()
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun <T> buildAnimation(
        scope: AnimatedContentScope<NavBackStackEntry>,
        builder: (forwardDirection: Boolean) -> T
    ): T {
        val isRoute = getStartingRoute(scope.initialState.destination)
        val tsRoute = getStartingRoute(scope.targetState.destination)

        val isIndex = bottomNavDestinations.indexOfFirst { it.route == isRoute }
        val tsIndex = bottomNavDestinations.indexOfFirst { it.route == tsRoute }

        return builder(
            tsIndex == -1 || isRoute == tsRoute || tsIndex > isIndex
        )
    }

    private fun getStartingRoute(destination: NavDestination): String {
        return destination.hierarchy.toList().let { it[it.lastIndex - 1] }.route.orEmpty()
    }
}