package com.bobbyesp.ui.motion

import android.graphics.Path
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bobbyesp.ui.motion.MotionConstants.DURATION_ENTER
import com.bobbyesp.ui.motion.MotionConstants.DURATION_EXIT
import com.bobbyesp.ui.motion.MotionConstants.initialOffset

fun NavGraphBuilder.fadeThroughComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    exitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    popEnterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    popExitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    content = content
)


private val path = Path().apply {
    moveTo(0f, 0f)
    cubicTo(0.05F, 0F, 0.133333F, 0.06F, 0.166666F, 0.4F)
    cubicTo(0.208333F, 0.82F, 0.25F, 1F, 1F, 1F)
}

val enterTween =
    tween<IntOffset>(durationMillis = DURATION_ENTER, easing = emphasizeEasing)
private val exitTween =
    tween<IntOffset>(durationMillis = DURATION_ENTER, easing = emphasizeEasing)
private val fadeTween = tween<Float>(durationMillis = DURATION_EXIT)

private val fadeSpec = fadeTween

fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        materialSharedAxisXIn(initialOffsetX = { (it * initialOffset).toInt() })
    },
    exitTransition = {
        materialSharedAxisXOut(targetOffsetX = { -(it * initialOffset).toInt() })
    },
    popEnterTransition = {
        materialSharedAxisXIn(initialOffsetX = { -(it * initialOffset).toInt() })
    },
    popExitTransition = {
        materialSharedAxisXOut(targetOffsetX = { (it * initialOffset).toInt() })
    },
    content = content
)

inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable<T>(
    deepLinks = deepLinks,
    enterTransition = {
        materialSharedAxisXIn(initialOffsetX = { (it * initialOffset).toInt() })
    },
    exitTransition = {
        materialSharedAxisXOut(targetOffsetX = { -(it * initialOffset).toInt() })
    },
    popEnterTransition = {
        materialSharedAxisXIn(initialOffsetX = { -(it * initialOffset).toInt() })
    },
    popExitTransition = {
        materialSharedAxisXOut(targetOffsetX = { (it * initialOffset).toInt() })
    },
    content = content
)



fun NavGraphBuilder.animatedComposableLegacy(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        slideInHorizontally(
            enterTween,
            initialOffsetX = { (it * initialOffset).toInt() }) + fadeIn(fadeSpec)
    },
    exitTransition = {
        slideOutHorizontally(
            exitTween,
            targetOffsetX = { -(it * initialOffset).toInt() }) + fadeOut(fadeSpec)
    },
    popEnterTransition = {
        slideInHorizontally(
            enterTween,
            initialOffsetX = { -(it * initialOffset).toInt() }) + fadeIn(fadeSpec)
    },
    popExitTransition = {
        slideOutHorizontally(
            exitTween,
            targetOffsetX = { (it * initialOffset).toInt() }) + fadeOut(fadeSpec)
    },
    content = content
)


fun NavGraphBuilder.animatedComposableVariant(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        slideInHorizontally(
            enterTween,
            initialOffsetX = { (it * initialOffset).toInt() }) + fadeIn(fadeSpec)
    },
    exitTransition = {
        fadeOut(fadeSpec)
    },
    popEnterTransition = {
        fadeIn(fadeSpec)
    },
    popExitTransition = {
        slideOutHorizontally(
            exitTween,
            targetOffsetX = { (it * initialOffset).toInt() }) + fadeOut(fadeSpec)
    },
    content = content
)

fun NavGraphBuilder.slideInVerticallyComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        slideInVertically(
            initialOffsetY = { it }, animationSpec = enterTween
        ) + fadeIn()
    },
    exitTransition = { slideOutVertically() },
    popEnterTransition = { slideInVertically() },
    popExitTransition = {
        slideOutVertically(
            targetOffsetY = { it },
            animationSpec = enterTween
        ) + fadeOut()
    },
    content = content
)

inline fun <reified T : Any> NavGraphBuilder.slideInVerticallyComposable(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable<T>(
    deepLinks = deepLinks,
    enterTransition = {
        slideInVertically(
            initialOffsetY = { it }, animationSpec = enterTween
        ) + fadeIn()
    },
    exitTransition = { slideOutVertically() },
    popEnterTransition = { slideInVertically() },
    popExitTransition = {
        slideOutVertically(
            targetOffsetY = { it },
            animationSpec = enterTween
        ) + fadeOut()
    },
    content = content
)