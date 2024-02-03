package com.bobbyesp.spowlo.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.bobbyesp.spowlo.data.local.model.SelectedSong
import com.bobbyesp.spowlo.features.spotifyApi.data.local.model.MetadataEntity
import com.bobbyesp.spowlo.ui.bottomSheets.player.PlayerAsBottomSheet
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalNotificationsManager
import com.bobbyesp.spowlo.ui.common.LocalPlayerAwareWindowInsets
import com.bobbyesp.spowlo.ui.common.MetadataEntityParamType
import com.bobbyesp.spowlo.ui.common.NavArgs
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.SelectedSongParamType
import com.bobbyesp.spowlo.ui.common.animatedComposable
import com.bobbyesp.spowlo.ui.common.animatedComposableVariant
import com.bobbyesp.spowlo.ui.common.slideInVerticallyComposable
import com.bobbyesp.spowlo.ui.components.bottomsheets.NavigationBarAnimationSpec
import com.bobbyesp.spowlo.ui.components.bottomsheets.rememberBottomSheetState
import com.bobbyesp.spowlo.ui.components.cards.notifications.SongDownloadNotification
import com.bobbyesp.spowlo.ui.ext.getParcelable
import com.bobbyesp.spowlo.ui.pages.LoginManagerViewModel
import com.bobbyesp.spowlo.ui.pages.downloader.tasks.DownloaderTasksPage
import com.bobbyesp.spowlo.ui.pages.home.HomePage
import com.bobbyesp.spowlo.ui.pages.home.HomePageViewModel
import com.bobbyesp.spowlo.ui.pages.home.notifications.NotificationsPage
import com.bobbyesp.spowlo.ui.pages.metadata_entities.MetadataEntityBinder
import com.bobbyesp.spowlo.ui.pages.profile.ProfilePage
import com.bobbyesp.spowlo.ui.pages.profile.ProfilePageViewModel
import com.bobbyesp.spowlo.ui.pages.search.SearchPage
import com.bobbyesp.spowlo.ui.pages.search.SearchViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.MediaStorePageViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.UtilitiesPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.main.LyricsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected.SelectedSongLyricsPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.selected.SelectedSongLyricsPageViewModel
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.TagEditorPage
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.ID3MetadataEditorPage
import com.bobbyesp.spowlo.ui.pages.utilities.tag_editor.editor.ID3MetadataEditorPageViewModel
import com.bobbyesp.spowlo.ui.theme.applyAlpha
import com.bobbyesp.spowlo.utils.ui.Constants
import com.bobbyesp.spowlo.utils.ui.Constants.MiniPlayerHeight
import com.bobbyesp.spowlo.utils.ui.Constants.NavigationBarHeight
import com.bobbyesp.spowlo.utils.ui.appBarScrollBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Navigator(
    loginManager: LoginManagerViewModel
) {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val layoutDirection = LocalLayoutDirection.current

    val configuration = LocalConfiguration.current
    val windowsInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    val notificationsManager = LocalNotificationsManager.current

    val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }
    val startInset = with(density) {
        windowsInsets.getLeft(density, layoutDirection = layoutDirection).toDp()
    }

    val routesToShowInBottomBar: List<Route> = remember {
        listOf(
            Route.HomeNavigator,
            Route.SearchNavigator,
            Route.UtilitiesNavigator,
            Route.ProfileNavigator,
            Route.DownloaderTasksNavigator
        )
    }

    val routesToShowNavBar = remember {
        listOf(
            Route.Home,
            Route.Search,
            Route.Utilities,
            Route.Profile,
            Route.DownloaderTasks
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

    var isLogged: Boolean? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
        isLogged = withContext(Dispatchers.IO) { loginManager.isLogged() }
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

        val navBarAsBottomSheet = rememberBottomSheetState(
            dismissedBound = 0.dp,
            collapsedBound = bottomInset + (if (shouldShowNavigationBar) NavigationBarHeight else 0.dp) + MiniPlayerHeight,
            expandedBound = maxHeight,
        )

        val navBarAwareWindowInsets = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                remember(bottomInset, shouldShowNavigationBar, navBarAsBottomSheet.isDismissed) {
                    var start = startInset
                    if (shouldShowNavigationBar) start += NavigationBarHeight
                    if (!navBarAsBottomSheet.isDismissed) start += MiniPlayerHeight
                    windowsInsets
                        .only(WindowInsetsSides.Vertical)
                        .add(WindowInsets(top = Constants.AppBarHeight, left = start))
                }
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                remember(bottomInset, shouldShowNavigationBar, navBarAsBottomSheet.isDismissed) {
                    var bottom = bottomInset
                    if (shouldShowNavigationBar) bottom += NavigationBarHeight
                    if (!navBarAsBottomSheet.isDismissed) bottom += MiniPlayerHeight
                    windowsInsets
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .add(WindowInsets(top = Constants.AppBarHeight, bottom = bottom))
                }
            }

            else -> {
                remember(bottomInset, shouldShowNavigationBar, navBarAsBottomSheet.isDismissed) {
                    var bottom = bottomInset
                    if (shouldShowNavigationBar) bottom += NavigationBarHeight
                    if (!navBarAsBottomSheet.isDismissed) bottom += MiniPlayerHeight
                    windowsInsets
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .add(WindowInsets(top = Constants.AppBarHeight, bottom = bottom))
                }
            }
        }

        val scrollBehavior = appBarScrollBehavior(
            canScroll = {
                (navBarAsBottomSheet.isCollapsed || navBarAsBottomSheet.isDismissed)
            }
        )

        val scope = rememberCoroutineScope()
        val notificationState by notificationsManager.getCurrentNotification()
            .collectAsStateWithLifecycle()

        val notificationVisible = notificationState != null
        if (notificationVisible) {
            DisposableEffect(notificationState) {
                val job = scope.launch {
                    delay(4000L)
                    notificationsManager.dismissNotification()
                }

                onDispose {
                    job.cancel()
                }
            }
        }

        CompositionLocalProvider(
            LocalPlayerAwareWindowInsets provides navBarAwareWindowInsets
        ) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                navController = navController,
                startDestination = Route.HomeNavigator.route,
                route = Route.MainHost.route,
            ) {
                navigation(
                    route = Route.HomeNavigator.route,
                    startDestination = Route.Home.route,
                ) {
                    animatedComposable(Route.Home.route) {
                        val viewModel = hiltViewModel<HomePageViewModel>()
                        HomePage(viewModel, isLogged ?: false, onLoginRequest = {
                            scope.launch {
                                runBlocking(Dispatchers.IO) { loginManager.login() }
                            }
                        })
                    }
                    animatedComposableVariant(Route.Notifications.route) {
                        NotificationsPage(notificationsManager) {
                            navController.popBackStack()
                        }
                    }
                }

                navigation(
                    route = Route.SearchNavigator.route,
                    startDestination = Route.Search.route,
                ) {
                    animatedComposable(Route.Search.route) {
                        val viewModel = hiltViewModel<SearchViewModel>()
                        SearchPage(viewModel = viewModel)
                    }
                }

                utilitiesNavigation(
                    mediaStorePageViewModel = mediaStoreViewModel
                )

                navigation(
                    route = Route.ProfileNavigator.route,
                    startDestination = Route.Profile.route,
                ) {
                    animatedComposable(Route.Profile.route) {
                        val viewModel = hiltViewModel<ProfilePageViewModel>()
                        ProfilePage(viewModel = viewModel)
                    }
                }
                animatedComposable(
                    route = Route.MetadataEntityViewer.route,
                    arguments = listOf(navArgument(NavArgs.MetadataEntitySelected.key) {
                        type = MetadataEntityParamType
                    })
                ) {
                    val selectedMetadataEntity =
                        it.getParcelable<MetadataEntity>(NavArgs.MetadataEntitySelected.key)

                    MetadataEntityBinder(metadataEntity = selectedMetadataEntity!!)
                }
                navigation(
                    route = Route.DownloaderTasksNavigator.route,
                    startDestination = Route.DownloaderTasks.route,
                ) {
                    animatedComposable(Route.DownloaderTasks.route) {
                        DownloaderTasksPage()
                    }
                }
                settingsNavigation()

            }
            PlayerAsBottomSheet(state = navBarAsBottomSheet, navController = navController)
            //--------------------------------- Navigation Bar (moved from Scaffold) ---------------------------------//
            val horizontalNavBar: @Composable () -> Unit = {
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
                                    (bottomInset + NavigationBarHeight) * navBarAsBottomSheet.progress.coerceIn(
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

                        val isEnabledBecauseOfLogin = if (route == Route.ProfileNavigator) isLogged == true else true
                        NavigationBarItem(
                            modifier = Modifier.animateContentSize().then(
                                if (isEnabledBecauseOfLogin) Modifier else Modifier.alpha(0.5f)
                            ),
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
                            },
                            alwaysShowLabel = false,
                            enabled = isEnabledBecauseOfLogin
                        )
                    }
                }
            }

            val verticalNavBar: @Composable () -> Unit = {
                NavigationRail(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset {
                            if (navigationBarHeight == 0.dp) {
                                IntOffset(
                                    y = 0, x = (startInset - NavigationBarHeight).roundToPx()
                                )
                            } else {
                                val slideOffset =
                                    (startInset - NavigationBarHeight) * navBarAsBottomSheet.progress.coerceIn(
                                        0f, 1f
                                    )
                                val hideOffset =
                                    (startInset + NavigationBarHeight) * (1 - navigationBarHeight / NavigationBarHeight)
                                IntOffset(
                                    y = 0, x = (slideOffset - hideOffset).roundToPx()
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
                        NavigationRailItem(
                            modifier = Modifier.animateContentSize(),
                            selected = isSelected,
                            onClick = onClick,
                            icon = {
                                Icon(
                                    imageVector = route.icon ?: return@NavigationRailItem,
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
                            }, alwaysShowLabel = false,
                            enabled = if (route == Route.ProfileNavigator) isLogged == true else true
                        )
                    }
                }
            }
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    verticalNavBar()
                }

                Configuration.ORIENTATION_PORTRAIT -> {
                    horizontalNavBar()
                }

                else -> {
                    horizontalNavBar()
                }
            }
        }
        AnimatedVisibility(
            visible = notificationVisible,
            enter = fadeIn(), // You can customize enter and exit animations
            exit = fadeOut() // As an example, fadeIn and fadeOut are used
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.applyAlpha(0.6f),
                                Color.Transparent
                            ),
                            endY = 500f
                        )
                    )
                    .fillMaxSize(), contentAlignment = Alignment.TopCenter
            ) {
                val notification = notificationState
                notification?.let {
                    if (notification.content != null) {
                        notification.content.let { it() }
                    } else {
                        val cardVisible = remember { mutableStateOf(false) }
                        if (!cardVisible.value) {
                            scope.launch {
                                delay(300) // Introduce a delay of 300ms
                                cardVisible.value = true
                            }
                        }

                        // Use transition to animate the card's appearance
                        val transition = updateTransition(
                            targetState = cardVisible.value,
                            label = "Card visibility transition"
                        )
                        val offset by transition.animateDp(
                            transitionSpec = { tween(durationMillis = 500) },
                            label = "Card offset transition",
                        ) { isVisible ->
                            if (isVisible) 40.dp else (-100).dp
                        }

                        SongDownloadNotification(
                            modifier = Modifier
                                .offset(y = offset)
                                .fillMaxWidth(0.8f),
                            notification = notification
                        )
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.utilitiesNavigation(
    mediaStorePageViewModel: MediaStorePageViewModel
) {
    navigation(
        route = Route.UtilitiesNavigator.route,
        startDestination = Route.Utilities.route,
    ) {
        animatedComposable(Route.Utilities.route) {
            UtilitiesPage()
        }
        animatedComposableVariant(Route.LyricsDownloader.route) {
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
            val selectedSongParcelable = it.getParcelable<SelectedSong>(NavArgs.SelectedSong.key)

            val viewModel = hiltViewModel<SelectedSongLyricsPageViewModel>()

            SelectedSongLyricsPage(viewModel, selectedSongParcelable!!)
        }

        animatedComposableVariant(Route.TagEditor.route) {
            TagEditorPage(mediaStorePageViewModel)
        }

        slideInVerticallyComposable(
            route = Route.TagEditor.Editor.route,
            arguments = listOf(navArgument(NavArgs.TagEditorSelectedSong.key) {
                type = SelectedSongParamType
            })
        ) {
            val selectedSongParcelable =
                it.getParcelable<SelectedSong>(NavArgs.TagEditorSelectedSong.key)

            val viewModel = hiltViewModel<ID3MetadataEditorPageViewModel>()

            ID3MetadataEditorPage(viewModel = viewModel, selectedSong = selectedSongParcelable!!)
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