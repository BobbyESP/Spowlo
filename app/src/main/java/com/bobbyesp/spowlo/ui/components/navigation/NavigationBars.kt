package com.bobbyesp.spowlo.ui.components.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.R
import com.bobbyesp.spowlo.ui.common.NavigationBarHeight
import com.bobbyesp.spowlo.ui.common.Route
import com.bobbyesp.spowlo.ui.common.routesToShowInBottomBar

val horizontalNavBar: @Composable BoxWithConstraintsScope.(
    navigationBarsProperties: NavigationBarsProperties
) -> Unit = { navBarProperties ->
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
) -> Unit = { navBarProperties ->
    val currentRootRoute = navBarProperties.currentRootRoute
    val navController = navBarProperties.navController
    val navigationBarHeight = navBarProperties.navBarCurrentHeight
    val startInset = navBarProperties.neededInset
    val navBarAsBottomSheet = navBarProperties.playerBottomSheetState

    NavigationRail(
        modifier = Modifier.offset {
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
        header = {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(
                    id = R.string.app_logo
                ),
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
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