package com.bobbyesp.ui.motion

import android.os.Build
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.bobbyesp.ui.motion.MotionConstants.InitialOffset

// This file is part of Seal (https://github.com/junkfood02/Seal). Thanks for the share!

fun <T> enterTween() = tween<T>(durationMillis = DURATION_ENTER, easing = EmphasizedEasing)

fun <T> exitTween() = tween<T>(durationMillis = DURATION_ENTER, easing = EmphasizedEasing)

private val fadeSpring =
    spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)

private val fadeTween = tween<Float>(durationMillis = DURATION_EXIT)
val fadeSpec = fadeTween

inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    deepLinks: List<NavDeepLink> = emptyList(),
    usePredictiveBack: Boolean = Build.VERSION.SDK_INT >= 34,
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) {
    if (usePredictiveBack) {
        animatedComposablePredictiveBack<T>(deepLinks, content)
    } else {
        animatedComposableLegacy<T>(deepLinks, content)
    }
}

inline fun <reified T : Any> NavGraphBuilder.animatedComposablePredictiveBack(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) =
    composable<T>(
        deepLinks = deepLinks,
        enterTransition = { materialSharedAxisXIn(initialOffsetX = { (it * 0.15f).toInt() }) },
        exitTransition = {
            materialSharedAxisXOut(targetOffsetX = { -(it * InitialOffset).toInt() })
        },
        popEnterTransition = {
            scaleIn(
                animationSpec = tween(durationMillis = 350, easing = EmphasizedDecelerate),
                initialScale = 0.9f,
            ) + materialSharedAxisXIn(initialOffsetX = { -(it * InitialOffset).toInt() })
        },
        popExitTransition = {
            materialSharedAxisXOut(targetOffsetX = { (it * InitialOffset).toInt() }) +
                    scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(durationMillis = 350, easing = EmphasizedAccelerate),
                    )
        },
        content = content,
    )

inline fun <reified T : Any> NavGraphBuilder.animatedComposableLegacy(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) =
    composable<T>(
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
        content = content,
    )

inline fun <reified T : Any> NavGraphBuilder.animatedComposableVariant(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) =
    composable<T>(
        deepLinks = deepLinks,
        enterTransition = {
            slideInHorizontally(enterTween(), initialOffsetX = { (it * InitialOffset).toInt() }) +
                    fadeIn(fadeSpec)
        },
        exitTransition = { fadeOut(fadeSpec) },
        popEnterTransition = { fadeIn(fadeSpec) },
        popExitTransition = {
            slideOutHorizontally(exitTween(), targetOffsetX = { (it * InitialOffset).toInt() }) +
                    fadeOut(fadeSpec)
        },
        content = content,
    )

val springSpec =
    spring(stiffness = Spring.StiffnessMedium, visibilityThreshold = IntOffset.VisibilityThreshold)

inline fun <reified T : Any> NavGraphBuilder.slideInVerticallyComposable(
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) =
    composable<T>(
        deepLinks = deepLinks,
        enterTransition = {
            slideInVertically(initialOffsetY = { it }, animationSpec = enterTween()) + fadeIn()
        },
        exitTransition = { slideOutVertically() },
        popEnterTransition = { slideInVertically() },
        popExitTransition = {
            slideOutVertically(targetOffsetY = { it }, animationSpec = enterTween()) + fadeOut()
        },
        content = content,
    )