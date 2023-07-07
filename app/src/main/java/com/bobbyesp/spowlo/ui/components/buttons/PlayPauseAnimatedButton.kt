package com.bobbyesp.spowlo.ui.components.buttons

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bobbyesp.spowlo.ui.theme.SpowloTheme

@Composable
fun PlayPauseAnimatedButton(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val radius = if (isPlaying || isPressed.value) {
        10.dp
    } else {
        5.dp
    }
    val cornerRadius = animateDpAsState(targetValue = radius, label = "Animated button shape")

    Surface(
        tonalElevation = 10.dp,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.value))
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onTertiary)
                .size(32.dp)
                .clip(RoundedCornerShape(cornerRadius.value))
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = isPlaying, label = "") { isPlaying ->
                when(isPlaying) {
                    true -> {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause button")
                    }
                    false -> {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play button")
                    }
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PlayPauseAnimationButtonPreview() {
    SpowloTheme {
        PlayPauseAnimatedButton(isPlaying = false, onClick = {})
    }
}

@Composable
fun RoundedPinBackground(
    modifier: Modifier = Modifier,
    size: Dp,
    backgroundColor: Color,
    onClicked: () -> Unit,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val radius = if (isPressed.value) {
        10.dp
    } else {
        size / 2f
    }
    val cornerRadius = animateDpAsState(targetValue = radius, label = "Animated button shape")

    Surface(
        tonalElevation = 10.dp,
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.value))
    ) {
        Box(
            modifier = Modifier
                .background(color = backgroundColor)
                .size(size)
                .clip(RoundedCornerShape(cornerRadius.value))
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple()
                ) { onClicked.invoke() },
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

