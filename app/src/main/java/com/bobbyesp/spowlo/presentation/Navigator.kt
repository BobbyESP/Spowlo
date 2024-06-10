package com.bobbyesp.spowlo.presentation

import android.content.Intent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowWidthSizeClass
import com.bobbyesp.spowlo.ext.formatAsClassToRoute
import com.bobbyesp.spowlo.features.notification_manager.domain.model.Notification
import com.bobbyesp.spowlo.features.notification_manager.presentation.NotificationsHandler
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.LocalNotificationManager
import com.bobbyesp.spowlo.presentation.common.LocalSnackbarHostState
import com.bobbyesp.spowlo.presentation.common.LocalWindowWidthState
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.spowlo.presentation.common.asProviderRoute
import com.bobbyesp.spowlo.presentation.common.mainRoutesForProvider
import com.bobbyesp.spowlo.presentation.components.OptionsDialog
import com.bobbyesp.spowlo.utils.navigation.cleanNavigate
import com.bobbyesp.spowlo.utils.navigation.navigateBack
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigator(
    handledIntent: Intent?,
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val focusManager = LocalFocusManager.current
    val windowWidthClass = LocalWindowWidthState.current

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
                ?: Route.Spotify::class.qualifiedName!!
        )
    }

    var currentProvider: Route by remember("provider") {
        mutableStateOf(Route.Spotify)
    }

    LaunchedEffect(currentProviderRoot) {
        currentProvider = currentProviderRoot.value.asProviderRoute()
    }

    val snackbarHostState = LocalSnackbarHostState.current

//    val (query, onQueryChange) = rememberSaveable(key = "searchQuery") {
//        mutableStateOf("")
//    }
//    var active by rememberSaveable {
//        mutableStateOf(false)
//    }
//    val onActiveChange: (Boolean) -> Unit = { newActive ->
//        active = newActive
//        if (!newActive) {
//            focusManager.clearFocus()
//            if (childRoutesForProvider(currentProviderRoot.value).fastAny { it.formatAsClassToRoute() == currentRoute.value }) {
//                onQueryChange("")
//            }
//        }
//    }
//
//    val onSearch: (String) -> Unit = {
//        if (it.isNotEmpty()) {
//            onActiveChange(false)
//            navController.navigate(Route.Spotify.SearchNavigator.Search(it))
//        }
//    }
//
//    var searchSource by remember {
//        mutableStateOf(Preferences.Enumerations.getValue(SEARCH_SOURCE, SearchSource.ONLINE))
//    }
//
//    var openSearchImmediately: Boolean by remember {
//        mutableStateOf(handledIntent?.action == MainActivity.ACTION_SEARCH)
//    }
//
//    val shouldShowSearchBar = true //TODO: Change this

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            mainRoutesForProvider(currentProvider).fastForEach { route ->
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
                        startDestination = Route.Spotify.HomeNavigator,
                    ) {
                        navigation<Route.Spotify.HomeNavigator>(
                            startDestination = Route.Spotify.HomeNavigator.Home,
                        ) {
                            composable<Route.Spotify.HomeNavigator.Home> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text("Hello, Spotify!")
                                    Button(onClick = {
                                        navController.cleanNavigate(Route.YoutubeMusic)
                                    }) {
                                        Text("Navigate to YouTube Music!")
                                    }
                                    val scope = rememberCoroutineScope()
                                    Button(onClick = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Hello, Snackbar!")
                                        }
                                    }) {
                                        Text("Show Snackbar")
                                    }
                                    val notificationsManager = LocalNotificationManager.current
                                    Button(onClick = {
                                        scope.launch {
                                            notificationsManager.showNotification(Notification(title = "Hello, Notification!"))
                                        }
                                    }) {
                                        Text("Show Snackbar")
                                    }
                                }
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
                                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
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
            NotificationsHandler()
//            AnimatedVisibility(
//                modifier = Modifier.align(Alignment.TopCenter),
//                visible = shouldShowSearchBar,
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                YtMusicAppSearchBar(
//                    query = query,
//                    onQueryChange = onQueryChange,
//                    onSearch = onSearch,
//                    active = active,
//                    onActiveChange = onActiveChange,
//                    searchSource = searchSource,
//                    onChangeSearchSource = { newSearchSource ->
//                        searchSource = newSearchSource
//                    },
//                )
//            }
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