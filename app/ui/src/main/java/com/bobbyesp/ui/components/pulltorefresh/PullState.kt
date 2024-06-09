package com.bobbyesp.ui.components.pulltorefresh

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun rememberPullState(
    config: PullStateConfig = PullStateConfig()
): PullState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val insetTop = WindowInsets.statusBars.getTop(density)

    return remember(insetTop, config, density, scope) {
        PullState(
            insetTop,
            config,
            density,
            scope
        )
    }
}

data class PullStateConfig(
    val heightRefreshing: Dp = 60.dp,
    val heightMax: Dp = 80.dp,
) {
    init {
        require(heightMax >= heightRefreshing)
    }
}

class PullState internal constructor(
    val maxInsetTop: Int,
    val config: PullStateConfig,
    private val density: Density,
    private val scope: CoroutineScope,
) {
    private val heightRefreshing = with(density) { config.heightRefreshing.toPx() }
    private val heightMax = with(density) { config.heightMax.toPx() }

    private val _offsetY = Animatable(0f)
    val offsetY: Float get() = _offsetY.value

    // 1f -> Refresh triggered on release
    val progressRefreshTrigger: Float get() = (offsetY / heightRefreshing).coerceIn(0f, 1f)

    // 1f -> Max drag amount reached
    val progressHeightMax: Float get() = (offsetY / heightMax).coerceIn(0f, 1f)

    // Use this for your content's top padding. Only relevant when app is drawing behind status bar
    val insetTop: Dp get() = with(density) { (maxInsetTop - maxInsetTop * progressRefreshTrigger).toDp() }

    // User drag in progress
    var isDragging by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var isEnabled by mutableStateOf(true)
        private set

    suspend fun settle(offsetY: Float) {
        _offsetY.animateTo(offsetY)
    }

    var isReloadFinished by mutableStateOf(false)
        private set

    fun finishRefresh(skipReloadFinished: Boolean = true) {
        isEnabled = false
        scope.launch {
            if (!skipReloadFinished) {
                settle(heightRefreshing) // keep the refreshing position
                isRefreshing = false
                isReloadFinished = true
                delay(2000) // hold the isReloadFinished state for 2 seconds
            }
            settle(0f) // go back to initial position
            isReloadFinished = false
            isEnabled = true
        }
    }

    val scrollConnection = object : NestedScrollConnection {
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            when {
                !isEnabled -> return Offset.Zero
                available.y > 0 && source == NestedScrollSource.UserInput -> {
                    // 1. User is dragging
                    // 2. Scrollable container reached the top (OR max drag reached and neither scroll container nor P2R are interested. Poor available Offset...)
                    // 3. There is still drag available that the scrollable container did not consume
                    // -> Start drag. Because next frame offsetY will be > 0f, onPreScroll will take over from here
                    isDragging = true
                    scope.launch {
                        _offsetY.snapTo((offsetY + available.y).coerceIn(0f, heightMax))
                    }
                }
            }

            return Offset.Zero
        }

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            when {
                !isEnabled -> return Offset.Zero
                offsetY > 0 && source == NestedScrollSource.UserInput -> {
                    // Consumes the drag as long as the indicator is visible
                    isDragging = true
                    val newOffset = offsetY + available.y

                    // Surplus drag amount is not consumed
                    val remaining = when {
                        newOffset > heightMax -> newOffset - heightMax
                        newOffset < 0f -> newOffset
                        else -> 0f
                    }

                    scope.launch {
                        _offsetY.snapTo(newOffset.coerceIn(0f, heightMax))
                    }

                    return Offset(0f, (available.y - remaining))
                }
            }

            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (!isEnabled) return Velocity.Zero

            isDragging = false

            when {
                // When refreshing and a drag stops, either settle to 0f or heightRefreshing,
                isRefreshing -> {
                    val target = when {
                        heightRefreshing - offsetY < heightRefreshing / 2 -> heightRefreshing
                        else -> 0f
                    }

                    scope.launch {
                        settle(target)
                    }

                    // Consume the velocity as long as the indicator is visible
                    return if (offsetY == 0f) Velocity.Zero else available
                }

                // Trigger refresh
                offsetY >= heightRefreshing -> {
                    isRefreshing = true
                    scope.launch {
                        settle(heightRefreshing)
                    }
                }

                // Drag cancelled, go back to 0f
                else -> {
                    scope.launch {
                        settle(0f)
                    }
                }
            }

            return Velocity.Zero
        }
    }
}