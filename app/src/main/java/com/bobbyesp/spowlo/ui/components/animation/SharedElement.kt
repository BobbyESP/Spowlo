@file:OptIn(ExperimentalComposeUiApi::class)

package com.bobbyesp.spowlo.ui.components.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.intermediateLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Modifier.animateSizeAndPosition(
    lookaheadScope: LookaheadScope,
    widthAnimationSpec: AnimationSpec<Float> = spring(1f, 400f, 0.5f),
    heightAnimationSpec: AnimationSpec<Float> = widthAnimationSpec,
    offsetXAnimationSpec: AnimationSpec<Float> = spring(1f, 200f, 0.5f),
    offsetYAnimationSpec: AnimationSpec<Float> = offsetXAnimationSpec
) = composed {
    // Creates a size animation
    var widthAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var heightAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }

    // Creates an offset animation
    var offsetXAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var offsetYAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var targetOffset: IntOffset? by remember {
        mutableStateOf(null)
    }

    this.intermediateLayout { measurable, constraints ->
        // When layout changes, the lookahead pass will calculate a new final size for the
        // child layout. This lookahead size can be used to animate the size
        // change, such that the animation starts from the current size and gradually
        // change towards `lookaheadSize`.
        if (lookaheadSize.width != widthAnimation?.targetValue?.roundToInt()) {
            widthAnimation?.run {
                launch {
                    animateTo(lookaheadSize.width.toFloat(), widthAnimationSpec)
                }
            } ?: Animatable(lookaheadSize.width.toFloat(), 0.5f).let {
                widthAnimation = it
            }
        }
        if (lookaheadSize.height != heightAnimation?.targetValue?.roundToInt()) {
            heightAnimation?.run {
                launch {
                    animateTo(lookaheadSize.height.toFloat(), heightAnimationSpec)
                }
            } ?: Animatable(lookaheadSize.height.toFloat(), 0.5f).let {
                heightAnimation = it
            }
        }
        val width = widthAnimation!!.value.roundToInt()
        val height = heightAnimation!!.value.roundToInt()
        // Creates a fixed set of constraints using the animated size
        val animatedConstraints = Constraints.fixed(width, height)
        // Measure child with animated constraints.
        val placeable = measurable.measure(animatedConstraints)

        layout(placeable.width, placeable.height) {
            // Converts coordinates of the current layout to LookaheadCoordinates
            val coordinates = coordinates
            if (coordinates != null) {
                // Calculates the target offset within the lookaheadScope
                val target = with(lookaheadScope) {
                    lookaheadScopeCoordinates
                        .localLookaheadPositionOf(coordinates)
                        .round()
                        .also { targetOffset = it }
                }

                // Uses the target offset to start an offset animation
                if (target.x != offsetXAnimation?.targetValue?.roundToInt()) {
                    offsetXAnimation?.run {
                        launch {
                            animateTo(target.x.toFloat(), offsetXAnimationSpec)
                        }
                    } ?: Animatable(target.x.toFloat(), 0.5f).let {
                        offsetXAnimation = it
                    }
                }
                if (target.y != offsetYAnimation?.targetValue?.roundToInt()) {
                    offsetYAnimation?.run {
                        launch {
                            animateTo(target.y.toFloat(), offsetYAnimationSpec)
                        }
                    } ?: Animatable(target.y.toFloat(), 0.5f).let {
                        offsetYAnimation = it
                    }
                }
                // Calculates the *current* offset within the given LookaheadScope
                val placementOffset = lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero)
                // Calculates the delta between animated position in scope and current
                // position in scope, and places the child at the delta offset. This puts
                // the child layout at the animated position.
                val x = (requireNotNull(offsetXAnimation).value - placementOffset.x).roundToInt()
                val y = (requireNotNull(offsetYAnimation).value - placementOffset.y).roundToInt()
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.animatePosition(
    lookaheadScope: LookaheadScope,
    widthAnimationSpec: AnimationSpec<Float> = spring(1f, 400f, 0.5f),
    heightAnimationSpec: AnimationSpec<Float> = widthAnimationSpec,
    offsetXAnimationSpec: AnimationSpec<Float> = tween(220),
    offsetYAnimationSpec: AnimationSpec<Float> = spring(1f, 200f, 0.5f)
) = composed {
    // Creates a size animation
    var widthAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var heightAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }

    // Creates an offset animation
    var offsetXAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var offsetYAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var targetOffset: IntOffset? by remember {
        mutableStateOf(null)
    }

    this.intermediateLayout { measurable, constraints ->
        // When layout changes, the lookahead pass will calculate a new final size for the
        // child layout. This lookahead size can be used to animate the size
        // change, such that the animation starts from the current size and gradually
        // change towards `lookaheadSize`.
        if (lookaheadSize.width != widthAnimation?.targetValue?.roundToInt()) {
            widthAnimation?.run {
                launch {
                    animateTo(lookaheadSize.width.toFloat(), widthAnimationSpec)
                }
            } ?: Animatable(lookaheadSize.width.toFloat(), 0.5f).let {
                widthAnimation = it
            }
        }
        if (lookaheadSize.height != heightAnimation?.targetValue?.roundToInt()) {
            heightAnimation?.run {
                launch {
                    animateTo(lookaheadSize.height.toFloat(), heightAnimationSpec)
                }
            } ?: Animatable(lookaheadSize.height.toFloat(), 0.5f).let {
                heightAnimation = it
            }
        }
        val width = widthAnimation!!.value.roundToInt()
        val height = heightAnimation!!.value.roundToInt()
        // Creates a fixed set of constraints using the animated size
        val animatedConstraints = Constraints.fixed(width, height)
        // Measure child with animated constraints.
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            // Converts coordinates of the current layout to LookaheadCoordinates
            val coordinates = coordinates
            if (coordinates != null) {
                // Calculates the target offset within the lookaheadScope
                val target = with(lookaheadScope) {
                    lookaheadScopeCoordinates
                        .localLookaheadPositionOf(coordinates)
                        .round()
                        .also { targetOffset = it }
                }

                // Uses the target offset to start an offset animation
                if (target.x != offsetXAnimation?.targetValue?.roundToInt()) {
                    offsetXAnimation?.run {
                        launch {
                            animateTo(target.x.toFloat(), offsetXAnimationSpec)
                        }
                    } ?: Animatable(target.x.toFloat(), 0.5f).let {
                        offsetXAnimation = it
                    }
                }
                if (target.y != offsetYAnimation?.targetValue?.roundToInt()) {
                    offsetYAnimation?.run {
                        launch {
                            animateTo(target.y.toFloat(), offsetYAnimationSpec)
                        }
                    } ?: Animatable(target.y.toFloat(), 0.5f).let {
                        offsetYAnimation = it
                    }
                }
                // Calculates the *current* offset within the given LookaheadScope
                val placementOffset = lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero)
                // Calculates the delta between animated position in scope and current
                // position in scope, and places the child at the delta offset. This puts
                // the child layout at the animated position.
                val x = (requireNotNull(offsetXAnimation).value - placementOffset.x).roundToInt()
                val y = (requireNotNull(offsetYAnimation).value - placementOffset.y).roundToInt()
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}

fun Modifier.animateSizeAfterLookaheadPass(
    widthAnimationSpec: AnimationSpec<Float> = spring(1f, 400f, 0.5f),
    heightAnimationSpec: AnimationSpec<Float> = widthAnimationSpec
) = composed {
    // Creates a size animation
    var widthAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var heightAnimation: Animatable<Float, AnimationVector1D>? by remember {
        mutableStateOf(null)
    }
    var lookaheadSize by remember {
        mutableStateOf<IntSize?>(null)
    }
    val coroutineScope = rememberCoroutineScope()

    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        val measuredSize = IntSize(placeable.width, placeable.height)
        val (width, height) = if (isLookingAhead) {
            // Record lookahead size if we are in lookahead pass. This lookahead size
            // will be used for size animation, such that the main measure pass will
            // gradually change size until it reaches the lookahead size.
            lookaheadSize = measuredSize
            listOf(measuredSize.width, measuredSize.height)
        } else {
            // Since we are in an explicit lookaheadScope, we know the lookahead pass
            // is guaranteed to happen, therefore the lookahead size that we recorded is
            // not null.
            val target = requireNotNull(lookaheadSize)
            val widthAnim = widthAnimation?.also {
                coroutineScope.launch { it.animateTo(target.width.toFloat(), widthAnimationSpec) }
            } ?: Animatable(target.width.toFloat(), 0.5f)
            widthAnimation = widthAnim
            val heightAnim = heightAnimation?.also {
                coroutineScope.launch { it.animateTo(target.height.toFloat(), heightAnimationSpec) }
            } ?: Animatable(target.height.toFloat(), 0.5f)
            heightAnimation = heightAnim
            // By returning the animated size only during main pass, we are allowing
            // lookahead pass to see the future layout past the animation.
            listOf(widthAnim.value.roundToInt(), heightAnim.value.roundToInt())
        }

        layout(width, height) {
            placeable.place(0, 0)
        }
    }
}

// Creates a custom modifier that animates the constraints and measures child with the
// animated constraints. This modifier is built on top of `Modifier.intermediateLayout`, which
// allows access to the lookahead size of the layout. A resize animation will be kicked off
// whenever the lookahead size changes, to animate children from current size to lookahead size.
// Fixed constraints created based on the animation value will be used to measure
// child, so the child layout gradually changes its size and potentially its child's placement
// to fit within the animated constraints.
fun Modifier.animateConstraints(
    animationSpec: AnimationSpec<IntSize> = spring(1f, 200f, IntSize.VisibilityThreshold)
) = composed {
    // Creates a size animation
    var sizeAnimation: Animatable<IntSize, AnimationVector2D>? by remember {
        mutableStateOf(null)
    }

    this.intermediateLayout { measurable, _ ->
        // When layout changes, the lookahead pass will calculate a new final size for the
        // child layout. This lookahead size can be used to animate the size
        // change, such that the animation starts from the current size and gradually
        // change towards `lookaheadSize`.
        if (lookaheadSize != sizeAnimation?.targetValue) {
            sizeAnimation?.run {
                launch { animateTo(lookaheadSize, animationSpec) }
            } ?: Animatable(lookaheadSize, IntSize.VectorConverter).let {
                sizeAnimation = it
            }
        }
        val (width, height) = sizeAnimation!!.value
        // Creates a fixed set of constraints using the animated size
        val animatedConstraints = Constraints.fixed(width, height)
        // Measure child with animated constraints.
        val placeable = measurable.measure(animatedConstraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

// Creates a custom modifier to animate the local position of the layout within the
// given LookaheadScope, whenever the relative position changes.
fun Modifier.animatePlacementInScope(
    lookaheadScope: LookaheadScope,
    animationSpec: AnimationSpec<IntOffset> = spring(1f, 200f, IntOffset.VisibilityThreshold)
) = composed {
    // Creates an offset animation
    var offsetAnimation: Animatable<IntOffset, AnimationVector2D>? by remember {
        mutableStateOf(null)
    }
    var targetOffset: IntOffset? by remember {
        mutableStateOf(null)
    }

    this.intermediateLayout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            // Converts coordinates of the current layout to LookaheadCoordinates
            val coordinates = coordinates
            if (coordinates != null) {
                // Calculates the target offset within the lookaheadScope
                val target = with(lookaheadScope) {
                    lookaheadScopeCoordinates
                        .localLookaheadPositionOf(coordinates)
                        .round()
                        .also { targetOffset = it }
                }

                // Uses the target offset to start an offset animation
                if (target != offsetAnimation?.targetValue) {
                    offsetAnimation?.run {
                        launch { animateTo(target, animationSpec) }
                    } ?: Animatable(target, IntOffset.VectorConverter).let {
                        offsetAnimation = it
                    }
                }
                // Calculates the *current* offset within the given LookaheadScope
                val placementOffset = lookaheadScopeCoordinates.localPositionOf(coordinates, Offset.Zero).round()
                // Calculates the delta between animated position in scope and current
                // position in scope, and places the child at the delta offset. This puts
                // the child layout at the animated position.
                val (x, y) = requireNotNull(offsetAnimation).run { value - placementOffset }
                placeable.place(x, y)
            } else {
                placeable.place(0, 0)
            }
        }
    }
}
