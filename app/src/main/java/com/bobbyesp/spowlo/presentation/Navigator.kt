package com.bobbyesp.spowlo.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.ext.formatAsClassToRoute
import com.bobbyesp.spowlo.features.search.SearchSource
import com.bobbyesp.spowlo.presentation.common.LocalNavController
import com.bobbyesp.spowlo.presentation.common.LocalSnackbarHostState
import com.bobbyesp.spowlo.presentation.common.Route
import com.bobbyesp.spowlo.presentation.common.childRoutesForProvider
import com.bobbyesp.spowlo.presentation.components.OptionsDialog
import com.bobbyesp.spowlo.presentation.components.ytmusic.YtMusicAppSearchBar
import com.bobbyesp.spowlo.utils.navigation.cleanNavigate
import com.bobbyesp.spowlo.utils.navigation.navigateBack
import com.bobbyesp.utilities.preferences.Preferences
import com.bobbyesp.utilities.preferences.PreferencesKeys.YouTube.SEARCH_SOURCE

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigator(
    handledIntent: Intent?,
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val focusManager = LocalFocusManager.current

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

    val currentProvider = rememberSaveable(navBackStackEntry, key = "currentProvider") {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.parent?.route ?: Route.Spotify::class.qualifiedName!!
        )
    }

    val snackbarHostState = LocalSnackbarHostState.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (query, onQueryChange) = rememberSaveable(key = "searchQuery") {
            mutableStateOf("")
        }
        var active by rememberSaveable {
            mutableStateOf(false)
        }
        val onActiveChange: (Boolean) -> Unit = { newActive ->
            active = newActive
            if (!newActive) {
                focusManager.clearFocus()
                if (childRoutesForProvider(currentProvider.value).fastAny { it.formatAsClassToRoute() == currentRoute.value }) {
                    onQueryChange("")
                }
            }
        }

        val onSearch: (String) -> Unit = {
            if (it.isNotEmpty()) {
                onActiveChange(false)
                navController.navigate(Route.Spotify.SearchNavigator.Search(it))
            }
        }

        var searchSource by remember {
            mutableStateOf(Preferences.Enumerations.getValue(SEARCH_SOURCE, SearchSource.ONLINE))
        }

        var openSearchImmediately: Boolean by remember {
            mutableStateOf(handledIntent?.action == MainActivity.ACTION_SEARCH)
        }

        val shouldShowSearchBar = true //TODO: Change this

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                ) { dataReceived ->
                    Snackbar(
                        modifier = Modifier,
                        snackbarData = dataReceived,
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                }
            },
        ) { scaffoldPadding ->
            SharedTransitionLayout {
                NavHost(
                    modifier = Modifier
                        .consumeWindowInsets(scaffoldPadding)
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
                                }
                            }

                            dialog<Route.Spotify.OptionsDialog> {
                                OptionsDialog {
                                    navController.navigateBack()
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

                            dialog<Route.YoutubeMusic.OptionsDialog>(
                                dialogProperties = DialogProperties(
                                    usePlatformDefaultWidth = false
                                )
                            ) {
                                OptionsDialog {
                                    navController.navigateBack()
                                }
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = shouldShowSearchBar,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            YtMusicAppSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                active = active,
                onActiveChange = onActiveChange,
                searchSource = searchSource,
                onChangeSearchSource = { newSearchSource ->
                    searchSource = newSearchSource
                },
            )
        }

    }
}