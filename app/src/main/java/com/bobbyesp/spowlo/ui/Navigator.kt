package com.bobbyesp.spowlo.ui

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.ui.bsPlayer.PlayerAsBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.common.NavArgs
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.SelectedSongParamType
import com.bobbyesp.spowlo.ui.common.slideInVerticallyComposable
import com.bobbyesp.spowlo.ui.components.bottomsheets.NavigationBarAnimationSpec
import com.bobbyesp.spowlo.ui.components.bottomsheets.rememberBottomSheetState
import com.bobbyesp.spowlo.ui.pages.home.HomePage
import com.bobbyesp.spowlo.ui.pages.home.HomePageViewModel
import com.bobbyesp.spowlo.ui.pages.profile.ProfilePage
import com.bobbyesp.spowlo.ui.pages.profile.ProfilePageViewModel
import com.bobbyesp.spowlo.ui.pages.search.SearchPage
import com.bobbyesp.spowlo.ui.pages.search.SearchViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.UtilitiesPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main.LyricsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main.MediaStorePageViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected.SelectedSongLyricsPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected.SelectedSongLyricsPageViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.TagEditorPage
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.ID3MetadataEditorPage
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.ID3MetadataEditorPageViewModel
import com.bobbyesp.spowlo.utils.ui.Constants.AppBarHeight
import com.bobbyesp.spowlo.utils.ui.Constants.MiniPlayerHeight
import com.bobbyesp.spowlo.utils.ui.Constants.NavigationBarHeight
import com.bobbyesp.spowlo.utils.ui.appBarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Navigator() {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val windowsInsets = WindowInsets.systemBars
    val density = LocalDensity.current
    val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }

    val routesToShowInBottomBar: List<Route> = remember {
        listOf(
            Route.HomeNavigator,
            Route.SearchNavigator,
            Route.UtilitiesNavigator,
            Route.ProfileNavigator
        )
    }

    val routesToShowNavBar = remember {
        listOf(
            Route.Home,
            Route.Search,
            Route.Utilities,
            Route.Profile,
        )
    }

    val currentRootRoute = rememberSaveable(navBackStackEntry, key = "currentRootRoute") {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Route.OnboardingPage.route
        )
    }

    val shouldShowNavigationBar = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.route == null ||
                routesToShowNavBar.fastAny { it.route == navBackStackEntry?.destination?.route }
    }

    val mediaStoreViewModel = hiltViewModel<MediaStorePageViewModel>()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val navigationBarHeight by animateDpAsState(
            targetValue = if (shouldShowNavigationBar) NavigationBarHeight else 0.dp,
            animationSpec = NavigationBarAnimationSpec,
            label = "Navigation bar animated height"
        )

        val playerBottomSheetState = rememberBottomSheetState(
            dismissedBound = 0.dp,
            collapsedBound = bottomInset + (if (shouldShowNavigationBar) NavigationBarHeight else 0.dp) + MiniPlayerHeight,
            expandedBound = maxHeight,
        )

        val playerAwareWindowInsets =
            remember(bottomInset, shouldShowNavigationBar, playerBottomSheetState.isDismissed) {
                var bottom = bottomInset
                if (shouldShowNavigationBar) bottom += NavigationBarHeight
                if (!playerBottomSheetState.isDismissed) bottom += MiniPlayerHeight
                windowsInsets
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .add(WindowInsets(top = AppBarHeight, bottom = bottom))
            }


        val scrollBehavior = appBarScrollBehavior(
            canScroll = {
                (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
            }
        )

        CompositionLocalProvider(
            LocalPlayerAwareWindowInsets provides playerAwareWindowInsets,
        ) {
            NavHost(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                navController = navController,
                startDestination = Route.HomeNavigator.route,
                route = Route.MainHost.route,
            ) {
                navigation(
                    route = Route.HomeNavigator.route,
                    startDestination = Route.Home.route,
                ) {
                    composable(Route.Home.route) {
                        val viewModel = hiltViewModel<HomePageViewModel>()
                        Box(modifier = Modifier.fillMaxSize()) {
                            HomePage(viewModel)
                        }
                    }
                }

                navigation(
                    route = Route.SearchNavigator.route,
                    startDestination = Route.Search.route,
                ) {
                    composable(Route.Search.route) {
                        val viewModel = hiltViewModel<SearchViewModel>()
                        SearchPage(viewModel = viewModel)
                    }
                }

                utilitiesNavigation(
                    navController = navController,
                    mediaStorePageViewModel = mediaStoreViewModel
                )

                navigation(
                    route = Route.ProfileNavigator.route,
                    startDestination = Route.Profile.route,
                ) {
                    composable(Route.Profile.route) {
                        val viewModel = hiltViewModel<ProfilePageViewModel>()
                        ProfilePage(viewModel = viewModel)
                    }
                }
            }
            PlayerAsBottomSheet(state = playerBottomSheetState, navController = navController)
            //--------------------------------- Navigation Bar (moved from Scaffold) ---------------------------------//
            NavigationBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset {
                        if (navigationBarHeight == 0.dp) {
                            IntOffset(
                                x = 0, y = (bottomInset + NavigationBarHeight).roundToPx()
                            )
                        } else {
                            val slideOffset =
                                (bottomInset + NavigationBarHeight) * playerBottomSheetState.progress.coerceIn(
                                    0f, 1f
                                )
                            val hideOffset =
                                (bottomInset + NavigationBarHeight) * (1 - navigationBarHeight / NavigationBarHeight)
                            IntOffset(
                                x = 0, y = (slideOffset + hideOffset).roundToPx()
                            )
                        }
                    },
            ) {
                routesToShowInBottomBar.forEach { route ->
                    val isSelected = currentRootRoute.value == route.route

                    val onClick = remember(isSelected, navController, route.route) {
                        {
                            if (!isSelected) {
                                navController.navigate(route.route) {
                                    popUpTo(Route.MainHost.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                    NavigationBarItem(
                        modifier = Modifier.animateContentSize(),
                        selected = isSelected,
                        onClick = onClick,
                        icon = {
                            Icon(
                                imageVector = route.icon ?: return@NavigationBarItem,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = {
                            Text(
                                text = route.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }, alwaysShowLabel = false
                    )
                }
            }
        }
    }
}

private fun NavGraphBuilder.utilitiesNavigation(
    navController: NavHostController,
    mediaStorePageViewModel: MediaStorePageViewModel
) {
    navigation(
        route = Route.UtilitiesNavigator.route,
        startDestination = Route.Utilities.route,
    ) {
        composable(Route.Utilities.route) {
            UtilitiesPage()
        }
        composable(Route.LyricsDownloader.route) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {

                LyricsDownloaderPage(mediaStorePageViewModel)
            }
        }

        slideInVerticallyComposable(
            route = Route.SelectedSongLyrics.route,
            arguments = listOf(navArgument(NavArgs.SelectedSong.key) {
                type = SelectedSongParamType
            })
        ) {

            @Suppress("DEPRECATION")
            val selectedSongParcelable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.arguments?.getParcelable(NavArgs.SelectedSong.key, SelectedSong::class.java)
                } else {
                    it.arguments?.getParcelable(NavArgs.SelectedSong.key)
                }

            val viewModel = hiltViewModel<SelectedSongLyricsPageViewModel>()

            SelectedSongLyricsPage(viewModel, selectedSongParcelable!!)
        }

        composable(Route.TagEditor.route) {

            TagEditorPage(mediaStorePageViewModel)
        }

        slideInVerticallyComposable(
            route = Route.TagEditor.Editor.route,
            arguments = listOf(navArgument(NavArgs.TagEditorSelectedSong.key) {
                type = SelectedSongParamType
            })
        ) {

            @Suppress("DEPRECATION")
            val selectedSongParcelable =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.arguments?.getParcelable(
                        NavArgs.TagEditorSelectedSong.key,
                        SelectedSong::class.java
                    )
                } else {
                    it.arguments?.getParcelable(NavArgs.TagEditorSelectedSong.key)
                }

            val viewModel = hiltViewModel<ID3MetadataEditorPageViewModel>()

            ID3MetadataEditorPage(viewModel = viewModel, selectedSong = selectedSongParcelable!!)
        }

        composable(Route.MiniplayerPage.route) {
            TODO()
        }
    }
}

private fun NavGraphBuilder.settingsNavigation() {
    navigation(
        route = Route.SettingsNavigator.route,
        startDestination = Route.Settings.route,
    ) {
        composable(Route.Settings.route) {
            TODO()
        }
    }
}