package com.bobbyesp.spowlo.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.features.search.SearchSource
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalSnackbarHostState
import com.bobbyesp.spowlo.ui.common.NavArgs
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.routesWhereToShowNavBar
import com.bobbyesp.spowlo.ui.components.AppSearchBar
import com.bobbyesp.spowlo.ui.components.OptionsDialog
import com.bobbyesp.utilities.preferences.Preferences
import com.bobbyesp.utilities.preferences.PreferencesKeys.YouTube.SEARCH_SOURCE

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Navigator(
    handledIntent: Intent?,
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val focusManager = LocalFocusManager.current

    val currentRootRoute = rememberSaveable(navBackStackEntry, key = "currentRootRoute") {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Route.HomeNavigator.route
        )
    }

    val currentRoute = rememberSaveable(navBackStackEntry, key = "currentRoute") {
        mutableStateOf(
            navBackStackEntry?.destination?.route ?: Route.HomeNavigator.Home.route
        )
    }

    val isCurrentRouteParent =
        routesWhereToShowNavBar.fastAny { it.route == navBackStackEntry?.destination?.route }

    val shouldShowNavigationBar = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.route == null || isCurrentRouteParent
    }

    val snackbarHostState = LocalSnackbarHostState.current

    val showSnackbarMessage: suspend (String) -> Unit = { message ->
        snackbarHostState.showSnackbar(message)
    }

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
                if (routesWhereToShowNavBar.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                    onQueryChange("")
                }
            }
        }

        val onSearch: (String) -> Unit = {
            if (it.isNotEmpty()) {
                onActiveChange(false)
                navController.navigate(Route.Search.createRoute(it))
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
            NavHost(
                modifier = Modifier
                    .consumeWindowInsets(scaffoldPadding)
                    .fillMaxWidth()
                    .align(Alignment.Center),
                navController = navController,
                startDestination = Route.HomeNavigator.route,
                route = Route.MainHost.route,
            ) {
                dialog(Route.OptionsDialog.route) {
                    OptionsDialog(
                        isPreview = false,
                        onExit = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = Route.Search.route,
                    arguments = listOf(navArgument(NavArgs.SearchQuery.key) {
                        type = NavType.StringType
                    })
                ) {

                }
                navigation(
                    route = Route.HomeNavigator.route,
                    startDestination = Route.HomeNavigator.Home.route
                ) {
                    composable(Route.HomeNavigator.Home.route) {
                        Scaffold(
                            modifier = Modifier
                        ) { paddingValues ->
                            Text(
                                modifier = Modifier.consumeWindowInsets(paddingValues),
                                text = "Hello, new Spowlo!"
                            )
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
            AppSearchBar(
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