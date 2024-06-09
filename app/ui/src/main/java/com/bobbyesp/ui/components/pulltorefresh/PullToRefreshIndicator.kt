package com.bobbyesp.ui.components.pulltorefresh

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bobbyesp.ui.R

@Composable
fun Indicator(
    pullState: PullState
) {
    val hapticFeedback = LocalHapticFeedback.current

    val scale = remember { Animatable(1f) }

    // Pop the indicator once shortly when reaching refresh trigger offset. Also trigger some haptic feedback
    LaunchedEffect(pullState.progressRefreshTrigger >= 1f) {
        if (pullState.progressRefreshTrigger >= 1f && !pullState.isRefreshing) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            scale.snapTo(1.05f)
            scale.animateTo(1.0f, tween(200))
        }
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .heightIn(
                24.dp,
                pullState.config.heightMax * pullState.progressHeightMax - pullState.insetTop
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .scale(scale.value),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pullState.isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp),
                    strokeWidth = 2.dp,
                )
            } else if (pullState.isReloadFinished) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = stringResource(R.string.successfully_refreshed),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                CircularProgressIndicator(
                    progress = { pullState.progressRefreshTrigger },
                    modifier = Modifier
                        .size(16.dp),
                    strokeWidth = 2.dp,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier,
                text = when {
                    pullState.isRefreshing -> stringResource(id = R.string.refreshing)
                    pullState.isReloadFinished -> stringResource(id = R.string.successfully_refreshed)
                    pullState.progressRefreshTrigger >= 1f -> stringResource(id = R.string.release_to_refresh)
                    else -> stringResource(id = R.string.pull_to_refresh)
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}