package com.bobbyesp.spowlo.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.bobbyesp.spowlo.MainActivity
import com.bobbyesp.spowlo.ui.common.AppBarHeight
import com.bobbyesp.spowlo.ui.common.CollapsedPlayerHeight
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.LocalNotificationManager
import com.bobbyesp.spowlo.ui.common.LocalPlayerInsetsAware
import com.bobbyesp.spowlo.ui.common.LocalSnackbarHostState
import com.bobbyesp.spowlo.ui.common.LocalWindowWidthState
import com.bobbyesp.spowlo.ui.common.NavigationBarAnimationSpec
import com.bobbyesp.spowlo.ui.common.NavigationBarHeight
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.routesWhereToShowNavBar
import com.bobbyesp.spowlo.ui.navComponents.NavigationBarsProperties
import com.bobbyesp.spowlo.ui.navComponents.horizontalNavBar
import com.bobbyesp.spowlo.ui.navComponents.verticalNavBar
import com.bobbyesp.ui.components.bottomsheets.dragable.rememberDraggableBottomSheetState
import com.bobbyesp.utilities.audio.model.SearchSource
import com.bobbyesp.utilities.utilities.preferences.Preferences
import com.bobbyesp.utilities.utilities.preferences.Preferences.getBoolean
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.PAUSE_SEARCH_HISTORY
import com.bobbyesp.utilities.utilities.preferences.PreferencesKeys.SEARCH_SOURCE
import com.bobbyesp.utilities.utilities.ui.applyAlpha
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
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

    val isCurrentRouteParent =
        routesWhereToShowNavBar.fastAny { it.route == navBackStackEntry?.destination?.route }

    val shouldShowNavigationBar = remember(navBackStackEntry) {
        navBackStackEntry?.destination?.route == null || isCurrentRouteParent
    }

    val snackbarHostState = LocalSnackbarHostState.current

    val showSnackbarMessage: suspend (String) -> Unit = { message ->
        snackbarHostState.showSnackbar(message)
    }

    val layoutDirection = LocalLayoutDirection.current
    val configuration = LocalConfiguration.current
    val widthState = LocalWindowWidthState.current

    val windowsInsets = WindowInsets.systemBars
    val density = LocalDensity.current
    val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }
    val startInset = with(density) {
        windowsInsets.getLeft(density, layoutDirection = layoutDirection).toDp()
    }

    val showNavigationRail = widthState != WindowWidthSizeClass.Compact

    val notificationsManager = LocalNotificationManager.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        /* Navigation and player */
        val navigationBarHeight by animateDpAsState(
            targetValue = if (shouldShowNavigationBar) NavigationBarHeight else 0.dp,
            animationSpec = NavigationBarAnimationSpec,
            label = "Navigation bar height animation"
        )

        val playerBottomSheetState = rememberDraggableBottomSheetState(
            dismissedBound = 0.dp,
            collapsedBound = bottomInset + (if (shouldShowNavigationBar) NavigationBarHeight else 0.dp) + CollapsedPlayerHeight,
            expandedBound = maxHeight,
            animationSpec = NavigationBarAnimationSpec,
        )

        val playerInsetsPortrait = remember(
            bottomInset, shouldShowNavigationBar, playerBottomSheetState.isDismissed
        ) {
            val bottom = bottomInset +
                    (if (shouldShowNavigationBar) NavigationBarHeight else 0.dp) +
                    (if (!playerBottomSheetState.isDismissed) CollapsedPlayerHeight else 0.dp)

            windowsInsets
                .only(sides = WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                .add(insets = WindowInsets(top = AppBarHeight, bottom = bottom))
        }

        val playerInsetsLandscape = remember(
            bottomInset, shouldShowNavigationBar, playerBottomSheetState.isDismissed
        ) {
            val start = startInset +
                    (if (shouldShowNavigationBar) NavigationBarHeight else 0.dp) +
                    (if (!playerBottomSheetState.isDismissed) CollapsedPlayerHeight else 0.dp)

            windowsInsets
                .only(sides = WindowInsetsSides.Vertical)
                .add(insets = WindowInsets(top = AppBarHeight, left = start))
        }

        /**
         * This is the insets that the player should be aware of.
         */
        val playerAwareInsets = when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> playerInsetsPortrait
            Configuration.ORIENTATION_LANDSCAPE -> playerInsetsLandscape
            else -> playerInsetsPortrait
        }

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
        var searchSource by remember {
            mutableStateOf(Preferences.EnumPrefs.getValue(SEARCH_SOURCE, SearchSource.ONLINE))
        }

        val searchBarFocusRequester = remember { FocusRequester() }

        val onSearch: (String) -> Unit = {
            if (it.isNotEmpty()) {
                onActiveChange(false)
                navController.navigate("search/${URLEncoder.encode(it, "UTF-8")}")
                if (!PAUSE_SEARCH_HISTORY.getBoolean()) {
//                    database.query {
//                        insert(SearchHistory(query = it))
//                    }
                }
            }
        }

        var openSearchImmediately: Boolean by remember {
            mutableStateOf(handledIntent?.action == MainActivity.ACTION_SEARCH)
        }

        val shouldShowSearchBar = true //TODO: Change this

        /*In-app notifications system*/
        val scope = rememberCoroutineScope()
        val currentNotification by notificationsManager.getCurrentNotification()
            .collectAsStateWithLifecycle()

        val notificationVisible = currentNotification != null
        if (notificationVisible) {
            DisposableEffect(currentNotification) {
                val job = scope.launch {
                    delay(4000L)
                    notificationsManager.dismissNotification()
                }

                onDispose {
                    job.cancel()
                }
            }
        }

        /**
         * This is the properties that the navigation bar needs to work nicely.
         */
        val navBarProperties = NavigationBarsProperties(
            currentRootRoute = currentRootRoute,
            navController = navController,
            navBarCurrentHeight = navigationBarHeight,
            neededInset = bottomInset,
            playerBottomSheetState = playerBottomSheetState,
        )

        CompositionLocalProvider(
            LocalPlayerInsetsAware provides playerAwareInsets,
        ) {
            NavHost(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                navController = navController,
                startDestination = Route.HomeNavigator.route,
                route = Route.MainHost.route,
            ) {
                navigation(
                    route = Route.HomeNavigator.route,
                    startDestination = Route.HomeNavigator.Home.route
                ) {
                    composable(Route.HomeNavigator.Home.route) {
                        Scaffold { paddingValues ->
                            Text(
                                modifier = Modifier.consumeWindowInsets(paddingValues),
                                text = "Hello, new Spowlo!"
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = shouldShowSearchBar, enter = fadeIn(), exit = fadeOut()
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    active = active,
                    onActiveChange = onActiveChange,
                ) {

                }
            }
        }

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                verticalNavBar(navBarProperties)
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                horizontalNavBar(navBarProperties)
            }

            else -> {
                horizontalNavBar(navBarProperties)
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
                val notification = currentNotification
                notification?.let {
                    if (notification.content != null) {
                        notification.content.invoke()
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

                        /*** Add here composable to show ***/
                    }
                }
            }
        }
    }
}