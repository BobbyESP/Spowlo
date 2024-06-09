package com.bobbyesp.ui.components.pulltorefresh

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * Made by Konstantin Klassen (https://twitter.com/Snokbert)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PullToRefreshLayout(
    modifier: Modifier = Modifier,
    pullState: PullState = rememberPullState(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null // Disable overscroll otherwise it consumes the drag before we get the chance
    ) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        ),
                        startY = -10f,
                        endY = pullState.progressRefreshTrigger * 120
                    )
                )
                .nestedScroll(pullState.scrollConnection),
        ) {
            Indicator(pullState = pullState)
            Column {
                // This invisible spacer height + current top inset is always equals max top inset to keep scroll speed constant
                Spacer(modifier = Modifier.height(LocalDensity.current.run { pullState.maxInsetTop.toDp() } - pullState.insetTop))

                Surface(
                    modifier = Modifier
                        .offset {
                            IntOffset(0, pullState.offsetY.toInt())
                        },
                    color = Color.Transparent,
                    shape = RoundedCornerShape(
                        topStart = 16.dp * pullState.progressRefreshTrigger,
                        topEnd = 16.dp * pullState.progressRefreshTrigger,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                ) {
                    content()
                }
            }
        }
    }
}