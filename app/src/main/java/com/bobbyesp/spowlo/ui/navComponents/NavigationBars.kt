package com.bobbyesp.spowlo.ui.navComponents

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bobbyesp.spowlo.ui.common.NavigationBarHeight
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.routesToShowInBottomBar
import com.bobbyesp.ui.components.bottomsheets.dragable.DraggableBottomSheetState

data class NavigationBarsProperties(
    val currentRootRoute: MutableState<String>,
    @Stable val navController: NavHostController,
    val navBarCurrentHeight: Dp,
    val neededInset: Dp,
    val playerBottomSheetState: DraggableBottomSheetState
)

val horizontalNavBar: @Composable BoxWithConstraintsScope.(
    navigationBarsProperties: NavigationBarsProperties
) -> Unit =
    { navBarProperties ->
        val currentRootRoute = navBarProperties.currentRootRoute
        val navController = navBarProperties.navController
        val navBarCurrentHeight = navBarProperties.navBarCurrentHeight
        val bottomInset = navBarProperties.neededInset
        val playerBottomSheetState = navBarProperties.playerBottomSheetState

        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset {
                    if (navBarCurrentHeight == 0.dp) {
                        IntOffset(
                            x = 0, y = (bottomInset + NavigationBarHeight).roundToPx()
                        )
                    } else {
                        val slideOffset =
                            (bottomInset + NavigationBarHeight) * playerBottomSheetState.progress.coerceIn(
                                0f, 1f
                            )
                        val hideOffset =
                            (bottomInset + NavigationBarHeight) * (1 - navBarCurrentHeight / NavigationBarHeight)
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
                        route.title?.run {
                            Text(
                                text = stringResource(id = this),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    alwaysShowLabel = false,
                    enabled = true
                )
            }
        }
    }

val verticalNavBar: @Composable BoxWithConstraintsScope.(
    navigationBarProperties: NavigationBarsProperties
) -> Unit =
    { navBarProperties ->
        val currentRootRoute = navBarProperties.currentRootRoute
        val navController = navBarProperties.navController
        val navigationBarHeight = navBarProperties.navBarCurrentHeight
        val startInset = navBarProperties.neededInset
        val navBarAsBottomSheet = navBarProperties.playerBottomSheetState

        NavigationRail(
            modifier = Modifier
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
                        route.title?.run {
                            Text(
                                text = stringResource(id = this),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }