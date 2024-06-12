package com.bobbyesp.spowlo.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowWidthSizeClass
import com.bobbyesp.spowlo.ext.formatAsClassToRoute
import com.bobbyesp.spowlo.features.notification_manager.presentation.NotificationsHandler
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.LocalSnackbarHostState
import com.bobbyesp.spowlo.presentation.common.LocalWindowWidthState
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.spowlo.presentation.common.asProviderRoute
import com.bobbyesp.spowlo.presentation.common.mainRoutesForProvider
import com.bobbyesp.spowlo.presentation.components.OptionsDialog
import com.bobbyesp.spowlo.presentation.components.spotify.search.SpAppSearchBarImpl
import com.bobbyesp.spowlo.presentation.components.ytmusic.search.YtMusicAppSearchBarImpl
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.AuthenticationPage
import com.bobbyesp.spowlo.presentation.pages.spotify.auth.SpotifyAuthManagerViewModel
import com.bobbyesp.spowlo.presentation.pages.spotify.home.HomePage
import com.bobbyesp.spowlo.utils.navigation.cleanNavigate
import com.bobbyesp.spowlo.utils.navigation.navigateBack
import com.bobbyesp.ui.util.appBarScrollBehavior

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    authManagerViewModel: SpotifyAuthManagerViewModel
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val focusManager = LocalFocusManager.current
    val windowWidthClass = LocalWindowWidthState.current

    val scope = rememberCoroutineScope()

    val currentRootRoute = rememberSaveable(navBackStackEntry, key = "currentRootRoute") {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route
        )
    }
    val currentRoute = rememberSaveable(navBackStackEntry, key = "currentRoute") {
        mutableStateOf(
            navBackStackEntry?.destination?.route
        )
    }

    val currentProviderRoot = rememberSaveable(navBackStackEntry, key = "currentProvider") {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.parent?.route
        )
    }

    var currentProvider: Route by remember("provider") {
        mutableStateOf(Route.Spotify)
    }

    LaunchedEffect(currentProviderRoot) {
        currentProvider = currentProviderRoot.value?.asProviderRoute() ?: Route.Spotify
    }

    val snackbarHostState = LocalSnackbarHostState.current
    val searchBarScrollBehavior = appBarScrollBehavior()
//
//    val shouldShowSearchBar = true //TODO: Change this
    val context = LocalContext.current

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            mainRoutesForProvider(currentProvider).forEach { route ->
                val routeClass = route.formatAsClassToRoute()
                item(
                    selected = routeClass == currentRootRoute.value,
                    onClick = {
                        navController.navigate(route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Square, contentDescription = null
                        )
                    },
                    label = {
                        Text(text = route.toString())
                    }
                )
            }
        }, layoutType = if (windowWidthClass == WindowWidthSizeClass.EXPANDED) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                currentWindowAdaptiveInfo()
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SharedTransitionLayout {
                NavHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    navController = navController,
                    startDestination = Route.Spotify,
                    route = Route.MainHost::class,
                ) {
                    navigation<Route.Spotify>(
                        startDestination = Route.Spotify.Auth,
                    ) {
                        composable<Route.Spotify.Auth> {
                            AuthenticationPage(authManagerViewModel)
                        }
                        navigation<Route.Spotify.HomeNavigator>(
                            startDestination = Route.Spotify.HomeNavigator.Home,
                        ) {
                            composable<Route.Spotify.HomeNavigator.Home> {
                                HomePage()
                            }
                        }

                        navigation<Route.Spotify.SearchNavigator>(
                            startDestination = Route.Spotify.SearchNavigator.Search::class,
                        ) {
                            composable<Route.Spotify.SearchNavigator.Search> {
                                val args = it.toRoute<Route.Spotify.SearchNavigator.Search>()
                                Text("Search results for ${args.query}")
                            }
                        }
                    }

                    navigation<Route.YoutubeMusic>(
                        startDestination = Route.YoutubeMusic.HomeNavigator,
                    ) {
                        navigation<Route.YoutubeMusic.HomeNavigator>(
                            startDestination = Route.YoutubeMusic.HomeNavigator.Home,
                        ) {
                            composable<Route.YoutubeMusic.HomeNavigator.Home> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text("Hello, YouTube Music!")
                                    Button(onClick = {
                                        navController.cleanNavigate(Route.Spotify)
                                    }) {
                                        Text("Navigate to Spotify!")
                                    }
                                }
                            }
                        }
                    }
                    dialog<Route.OptionsDialog>(
                        dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(0.9f)) {
                            OptionsDialog {
                                navController.navigateBack()
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if(currentProvider == Route.Spotify) {
                    SpAppSearchBarImpl(searchBarScrollBehavior)
                } else {
                    YtMusicAppSearchBarImpl(searchBarScrollBehavior)
                }
            }

            NotificationsHandler()
            SnackbarHost(
                modifier = Modifier.align(Alignment.BottomCenter),
                hostState = snackbarHostState
            ) { dataReceived ->
                Snackbar(
                    modifier = Modifier,
                    snackbarData = dataReceived
                )
            }
        }
    }
}