package com.bobbyesp.spowlo.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.bobbyesp.spowlo.ui.common.LocalNavController
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.components.cards.AppUtilityCard
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.LyricsDownloaderPage
import com.bobbyesp.spowlo.ui.pages.utilities.lyrics_downloader.LyricsDownloaderPageViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

private const val TAG = "Navigator"

@OptIn(
    ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun Navigator() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val onBackPressed: () -> Unit = { navController.popBackStack() }

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
                        modifier = Modifier
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

                            NavigationBarItem(selected = isSelected, onClick = onClick, icon = {
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
                            })

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

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                            ) {
                                Button(
                                    modifier = Modifier,
                                    onClick = {
                                        error("Crash!")
                                    }
                                ) {
                                    Text(text = "Tap to crash")
                                }
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
                                            utilityName = "Synced Lyrics",
                                            icon = Icons.Default.Lyrics
                                        ) {
                                            navController.navigate(Route.LyricsDownloaderPage.route)
                                        }
                                    }
                                    item {
                                        AppUtilityCard(
                                            utilityName = "Equalizer",
                                            icon = Icons.Default.Equalizer
                                        ) {
                                            //navController.navigate(Route.Equalizer.route)
                                        }
                                    }
                                    item {
                                        AppUtilityCard(
                                            utilityName = "Sleep Timer",
                                            icon = Icons.Default.Timer
                                        ) {
                                            //navController.navigate(Route.SleepTimer.route)
                                        }
                                    }
                                }
//                                val localContext = LocalContext.current
//                                val songsToShow by rememberSaveable(key = "songsToShow") {
//                                    mutableStateOf(
//                                        MediaStoreReceiver.getAllSongsFromMediaStore(
//                                            localContext
//                                        )
//                                    )
//                                }
//                                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                                    items(songsToShow.size) { index ->
//                                        if (songsToShow[index].albumArtPath != null) {
//                                            AsyncImageImpl(
//                                                modifier = Modifier
//                                                    .padding(16.dp)
//                                                    .size(84.dp)
//                                                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
//                                                    .clip(MaterialTheme.shapes.small),
//                                                model = songsToShow[index].albumArtPath!!,
//                                                contentDescription = "Song cover",
//                                                contentScale = ContentScale.Crop,
//                                                isPreview = false
//                                            )
//                                        }
//                                    }
//                                }
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