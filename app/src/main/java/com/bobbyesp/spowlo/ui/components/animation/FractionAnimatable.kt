package com.bobbyesp.spowlo.ui.components.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
class FractionAnimatable(
    initialValue: Float,
    private val scale: Float = 10000f,
    visibilityThreshold: Float = 0.1f
) {
    private val percentage = Animatable(initialValue * scale, visibilityThreshold)

    val value by derivedStateOf {
        percentage.value / scale
    }

    val targetValue by derivedStateOf {
        percentage.targetValue / scale
    }

    val velocity by derivedStateOf {
        percentage.velocity / scale
    }

    val isRunning by derivedStateOf {
        percentage.isRunning
    }

    suspend fun animateTo(
        targetValue: Float,
        animationSpec: AnimationSpec<Float> = spring(),
        initialVelocity: Float = percentage.velocity / scale,
        block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null
    ): AnimationResult<Float, AnimationVector1D> {
        return percentage.animateTo(
            targetValue * scale,
            animationSpec,
            initialVelocity * scale,
            block
        )
    }

    suspend fun snapTo(targetValue: Float) {
        percentage.snapTo(targetValue * scale)
    }
}
