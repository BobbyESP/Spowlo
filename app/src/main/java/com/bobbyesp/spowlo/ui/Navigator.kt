package com.bobbyesp.spowlo.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.SpotifyApiRequests
import com.bobbyesp.spowlo.features.spotifyApi.data.remote.searching.TrackSearch
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.cards.AppUtilityCard
import com.bobbyesp.spowlo.ui.components.cards.SpotifySongCard
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.LyricsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.LyricsDownloaderPageViewModel

private const val TAG = "Navigator"

@OptIn(
    ExperimentalLayoutApi::class
)
@Composable
fun Navigator() {

    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRootRoute = remember(navBackStackEntry) {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Route.OnboardingPage.route
        )
    }

    val shouldHideNavBar = remember(navBackStackEntry) {
        mutableStateOf(
            navBackStackEntry?.destination?.route in listOf(
                Route.OnboardingPage.route,
            )
        )
    }

    val routesToShow: List<Route> = listOf(Route.HomeNavigator, Route.UtilitiesNavigator)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            bottomBar = {
                AnimatedVisibility(
                    visible = !shouldHideNavBar.value,
                ) {
                    NavigationBar(
                        modifier = Modifier.height(72.dp)
                    ) {
                        routesToShow.forEach { route ->
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
                                modifier = Modifier.padding(top = 6.dp),
                                selected = isSelected,
                                onClick = onClick,
                                icon = {
                                    Icon(
                                        imageVector = route.icon ?: return@NavigationBarItem,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }, label = {
                                    Text(
                                        text = route.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            )
                        }
                    }
                }
            }) { paddingValues ->
            NavHost(
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues),
                navController = navController,
                startDestination = Route.HomeNavigator.route,
                route = Route.MainHost.route,
            ) {
                navigation(
                    route = Route.HomeNavigator.route,
                    startDestination = Route.Home.route,
                ) {
                    composable(Route.Home.route) {
                        val api = SpotifyApiRequests

                        var tracks by rememberSaveable {
                            mutableStateOf<List<Track>>(emptyList())
                        }

                        LaunchedEffect(true) {
                            tracks = TrackSearch(api.provideSpotifyApi()).search("Faded Alan Walker")
                        }
                        Text(text = "Showing result for: Faded Alan Walker", modifier = Modifier.padding(8.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(150.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(tracks) { track ->
                                SpotifySongCard(track = track, onClick = { })
                            }
                        }
                    }
                }

                navigation(
                    route = Route.UtilitiesNavigator.route,
                    startDestination = Route.Utilities.route,
                ) {
                    composable(Route.Utilities.route) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Column(
                                modifier = Modifier,
                            ) {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(150.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    item {
                                        AppUtilityCard(
                                            utilityName = stringResource(id = R.string.lyrics_downloader),
                                            icon = Icons.Default.Lyrics
                                        ) {
                                            navController.navigate(Route.LyricsDownloaderPage.route)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    composable(Route.LyricsDownloaderPage.route) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val viewModel = hiltViewModel<LyricsDownloaderPageViewModel>()
                            LyricsDownloaderPage(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun localAsset(@DrawableRes id: Int) = ImageVector.vectorResource(id = id)