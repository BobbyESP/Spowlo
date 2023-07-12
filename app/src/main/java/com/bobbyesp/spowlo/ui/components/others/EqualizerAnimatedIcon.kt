package com.bobbyesp.spowlo.ui.components.others

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

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

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EqualizerAnimatedIconPreview() {
    SpowloTheme {
        EqualizerAnimatedIcon(lineColor = Color(0xFF1DB954))
    }
}

val FastOutFastInEasing: Easing = CubicBezierEasing(0.4f, 0f, 1f, 1f)