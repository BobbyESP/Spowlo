@file:OptIn(ExperimentalFoundationApi::class)

package com.bobbyesp.spowlo.ui.components.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

fun Modifier.smoothVerticalOverscroll(
    state: ScrollableState,
    animationSpec: AnimationSpec<Float> = spring(0.8f, 100f)
): Modifier = composed {
    val scope = rememberCoroutineScope()
    val overscrollOffset = remember { Animatable(0f, 0.5f) }
    val connection = remember(scope) {
        SmoothVerticalOverscrollNestedScrollConnection(state, scope, overscrollOffset, animationSpec)
    }
    this
        .nestedScroll(connection)
        .graphicsLayer { translationY = overscrollOffset.value }
}

fun Modifier.smoothVerticalScroll(
    state: ScrollState,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    animationSpec: AnimationSpec<Float> = spring(0.8f, 100f)
) = composed {
    val scope = rememberCoroutineScope()
    val overscrollOffset = remember { Animatable(0f, 0.5f) }
    val connection = remember(scope) {
        SmoothVerticalOverscrollNestedScrollConnection(state, scope, overscrollOffset, animationSpec)
    }
    this
        .nestedScroll(connection)
        .verticalScroll(
            state = state,
            enabled = enabled,
            flingBehavior = flingBehavior,
            reverseScrolling = reverseScrolling
        )
        .graphicsLayer { translationY = overscrollOffset.value }
}

fun Modifier.smoothVerticalScroll(
    state: ScrollState,
    overscrollOffset: Animatable<Float, AnimationVector1D>,
    enabled: Boolean = true,
    flingBehavior: FlingBehavior? = null,
    reverseScrolling: Boolean = false,
    animationSpec: AnimationSpec<Float> = spring(0.8f, 100f)
) = composed {
    val scope = rememberCoroutineScope()
    val connection = remember(scope) {
        SmoothVerticalOverscrollNestedScrollConnection(state, scope, overscrollOffset, animationSpec)
    }
    this
        .nestedScroll(connection)
        .verticalScroll(
            state = state,
            enabled = enabled,
            flingBehavior = flingBehavior,
            reverseScrolling = reverseScrolling
        )
        .graphicsLayer { translationY = overscrollOffset.value }
}

private class SmoothVerticalOverscrollNestedScrollConnection(
    private val state: ScrollableState,
    private val scope: CoroutineScope,
    private val overscrollOffset: Animatable<Float, AnimationVector1D>,
    private val animationSpec: AnimationSpec<Float> = spring(0.8f, 100f, 0.5f)
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (available.isUnspecified) return Offset.Zero
        if (!state.canScrollBackward && overscrollOffset.value > 0f && available.y < 0f) {
            val newValue = (overscrollOffset.value + available.y).coerceAtLeast(0f)
            scope.launch {
                overscrollOffset.snapTo(newValue)
            }
            return available
        } else if (!state.canScrollForward && overscrollOffset.value < 0f && available.y > 0f) {
            val newValue = (overscrollOffset.value + available.y).coerceAtMost(0f)
            scope.launch {
                overscrollOffset.snapTo(newValue)
            }
            return available
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (!state.canScrollBackward && available.y > 0f) {
            val newValue = (overscrollOffset.value + available.y).coerceAtLeast(0f)
            scope.launch {
                overscrollOffset.snapTo(newValue)
            }
        } else if (!state.canScrollForward && available.y < 0f) {
            val newValue = (overscrollOffset.value + available.y).coerceAtMost(0f)
            scope.launch {
                overscrollOffset.snapTo(newValue)
            }
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (overscrollOffset.value != 0f &&
            (!state.canScrollBackward || !state.canScrollForward)
        ) {
            scope.launch {
                overscrollOffset.animateTo(0f, animationSpec, available.y)
            }
            return available
        }
        return Velocity.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (overscrollOffset.value != 0f && available.y != 0f &&
            (!state.canScrollBackward || !state.canScrollForward)
        ) {
            scope.launch {
                overscrollOffset.animateTo(0f, animationSpec, available.y)
            }
        }
        return Velocity.Zero
    }
}

// our custom offset overscroll that offset the element it is applied to when we hit the bound
// on the scrollable container.
/**
 * [https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/foundation/foundation/samples/src/main/java/androidx/compose/foundation/samples/OverscrollSample.kt]
 */
class OffsetOverscrollEffect(private val scope: CoroutineScope) : OverscrollEffect {
    private val overscrollOffset = Animatable(0f)
    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset
    ): Offset {
        // in pre scroll we relax the overscroll if needed
        // relaxation: when we are in progress of the overscroll and user scrolls in the
        // different direction = subtract the overscroll first
        val sameDirection = sign(delta.y) == sign(overscrollOffset.value)
        val consumedByPreScroll = if (abs(overscrollOffset.value) > 0.5 && !sameDirection) {
            val prevOverscrollValue = overscrollOffset.value
            val newOverscrollValue = overscrollOffset.value + delta.y
            if (sign(prevOverscrollValue) != sign(newOverscrollValue)) {
                // sign changed, coerce to start scrolling and exit
                scope.launch { overscrollOffset.snapTo(0f) }
                Offset(x = 0f, y = delta.y + prevOverscrollValue)
            } else {
                scope.launch {
                    overscrollOffset.snapTo(overscrollOffset.value + delta.y)
                }
                delta.copy(x = 0f)
            }
        } else {
            Offset.Zero
        }
        val leftForScroll = delta - consumedByPreScroll
        val consumedByScroll = performScroll(leftForScroll)
        val overscrollDelta = leftForScroll - consumedByScroll
        // if it is a drag, not a fling, add the delta left to our over scroll value
        if (abs(overscrollDelta.y) > 0.5 && source == NestedScrollSource.Drag) {
            scope.launch {
                // multiply by 0.1 for the sake of parallax effect
                overscrollOffset.snapTo(overscrollOffset.value + overscrollDelta.y * 0.1f)
            }
        }
        return consumedByPreScroll + consumedByScroll
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        val consumed = performFling(velocity)
        // when the fling happens - we just gradually animate our overscroll to 0
        val remaining = velocity - consumed
        overscrollOffset.animateTo(
            targetValue = 0f,
            initialVelocity = remaining.y,
            animationSpec = spring()
        )
    }

    override val isInProgress: Boolean
        get() = overscrollOffset.value != 0f

    // as we're building an offset modifiers, let's offset of our value we calculated
    override val effectModifier: Modifier = Modifier.graphicsLayer {
        translationY = overscrollOffset.value
    }
}
