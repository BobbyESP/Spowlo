package com.bobbyesp.spowlo.ui.components.others

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.SpowloTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun EqualizerAnimatedIcon(
    modifier: Modifier = Modifier,
    lineColor: Color
) {
    val transition = rememberInfiniteTransition(label = "")

    val line1Height by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        ), label = ""
    )

    val line2Height by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        ), label = ""
    )

    val line3Height by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        ), label = ""
    )

    Row(
        modifier = modifier
            .background(Color.Transparent)
            .size(45.dp)
            .rotate(180f),
        horizontalArrangement = Arrangement.Center,
    ) {
        Line(
            modifier = Modifier.padding(end = 5.dp),
            height = line1Height,
            color = lineColor)
        Line(
            modifier = Modifier.padding(end = 5.dp),
            height = line2Height,
            color = lineColor
        )
        Line(height = line3Height, color = lineColor)
    }
}

@Composable
fun Line(
    modifier: Modifier = Modifier,
    height: Float,
    color: Color) {
    Box(
        modifier = modifier
            .fillMaxHeight(height)
            .width(10.dp)
            .background(color)
    )
}

/**
 * Credits to InnerTune
 */
@Composable
fun PlayingIndicator(
    color: Color,
    modifier: Modifier = Modifier,
    bars: Int = 3,
    barWidth: Dp = 4.dp,
    cornerRadius: Dp = 2.dp,
) {
    val animatables = remember {
        List(bars) {
            Animatable(0.1f)
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        animatables.forEach { animatable ->
            launch {
                while (true) {
                    animatable.animateTo(Random.nextFloat() * 0.9f + 0.1f)
                    delay(50)
                }
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        animatables.forEach { animatable ->
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(barWidth)
            ) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x = 0f, y = size.height * (1 - animatable.value)),
                    size = size.copy(height = animatable.value * size.height),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            }
        }
    }
}

@Composable
fun PlayingIndicatorBox(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    playWhenReady: Boolean,
    color: Color = Color.White,
) {
    AnimatedVisibility(
        visible = isActive,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            if (playWhenReady) {
                PlayingIndicator(
                    color = color,
                    modifier = Modifier.height(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = color
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EqualizerAnimatedIconPreview() {
    SpowloTheme {
        EqualizerAnimatedIcon(lineColor = Color(0xFF1DB954))
    }
}

val FastOutFastInEasing: Easing = CubicBezierEasing(0.4f, 0f, 1f, 1f)