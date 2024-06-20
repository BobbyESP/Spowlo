package com.bobbyesp.ui.components.slider

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@Composable
fun RubberBandSlider(
    modifier: Modifier
) {
    /** y value of the pointer. Excessive drag amount beyond the slider's bounds is added to overdrag **/
    var offsetY by remember { mutableFloatStateOf(0f) }

    /** Height of the slider **/
    var height by remember { mutableFloatStateOf(0f) }

    /** Slider progress between 0f and 1f **/
    val progress = remember { Animatable(0.5f) }

    /** Excessive drag amount beyond the slider's bounds **/
    val overdrag = remember { Animatable(0f) }

    val scope = rememberCoroutineScope()

    fun updateProgress(height: Float, offsetY: Float) {
        scope.launch {
            progress.animateTo(1f - offsetY / height)
        }
    }

    // Background
    Box(
        modifier = modifier
            .onGloballyPositioned {
                height = it.size.height.toFloat()
            }
            // Pointer DOWN
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    offsetY = it.y
                    updateProgress(height, offsetY)
                })
            }
            // Pointer MOVE
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState {
                    offsetY += it
                    updateProgress(height, offsetY)
                    when {
                        offsetY <= 0f -> offsetY
                        offsetY > height -> offsetY - height
                        else -> 0f
                    }.let {
                        scope.launch {
                            overdrag.animateTo(it)
                        }
                    }
                },
                // Pointer UP
                onDragStopped = {
                    scope.launch {
                        overdrag.animateTo(0f)
                    }
                }
            )
            .graphicsLayer {
                // Default is (0.5f, 0.5f) which would scale the shape around the center, uniformly in all directions. We want the slider to expand
                // in direction of the drag
                transformOrigin = TransformOrigin(0.5f, if (overdrag.value < 0) 1f else 0f)

                // Slow down the overdrag and take the absolute value because we want the same stretch in both directions
                val adjustedOverdrag = 1f - height / (height - sqrt(overdrag.value.absoluteValue) * 2f)

                // Stretch in y direction
                scaleY = 1f - adjustedOverdrag

                // Shrink in x direction
                scaleX = 1f + adjustedOverdrag * 1.5f

                // Move the slider slightly in drag direction
                translationY = sqrt(overdrag.value.absoluteValue * 5f) * if (overdrag.value < 0) -1f else 1f

                shape = RoundedCornerShape(24.dp)
                clip = true
            }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.BottomStart
    ) {
        // Foreground
        Box(modifier = Modifier
            .fillMaxHeight(progress.value)
            .fillMaxWidth(1f)
            .background(MaterialTheme.colorScheme.primary)
        )
    }
}