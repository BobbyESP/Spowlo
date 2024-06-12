package com.bobbyesp.ui.motion

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
import com.bobbyesp.ui.motion.MotionConstants.DURATION_EXIT
import com.bobbyesp.ui.motion.MotionConstants.InitialOffset

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

val enterTween = tweenEnter<IntOffset>()
val exitTween = tweenExit<IntOffset>()
val fadeTween = tween<Float>(durationMillis = DURATION_EXIT)


inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable<T>(
    deepLinks = deepLinks,
    enterTransition = {
        materialSharedAxisXIn(initialOffsetX = { (it * InitialOffset).toInt() })
    },
    exitTransition = {
        materialSharedAxisXOut(targetOffsetX = { -(it * InitialOffset).toInt() })
    },
    popEnterTransition = {
        materialSharedAxisXIn(initialOffsetX = { -(it * InitialOffset).toInt() })
    },
    popExitTransition = {
        materialSharedAxisXOut(targetOffsetX = { (it * InitialOffset).toInt() })
    },
    content = content
)

inline fun <reified T: Any> NavGraphBuilder.animatedComposableVariant(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable<T>(
    deepLinks = deepLinks,
    enterTransition = {
        slideInHorizontally(
            enterTween,
            initialOffsetX = { (it * InitialOffset).toInt() }) + fadeIn(fadeTween)
    },
    exitTransition = {
        fadeOut(fadeTween)
    },
    popEnterTransition = {
        fadeIn(fadeTween)
    },
    popExitTransition = {
        slideOutHorizontally(
            exitTween,
            targetOffsetX = { (it * InitialOffset).toInt() }) + fadeOut(fadeTween)
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