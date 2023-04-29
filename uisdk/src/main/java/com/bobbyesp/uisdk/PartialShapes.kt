package com.bobbyesp.uisdk

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
class PartialShapes(
    val largeTopLeftShape: CornerBasedShape = RoundedCornerShape(topStart = 16.0.dp),
    val largeTopRightShape: CornerBasedShape = RoundedCornerShape(topEnd = 16.0.dp),
    val largeTopShape: CornerBasedShape = RoundedCornerShape(topStart = 16.0.dp, topEnd = 16.0.dp),
    val largeBottomShape: CornerBasedShape = RoundedCornerShape(bottomStart = 16.0.dp, bottomEnd = 16.0.dp),
)

internal val LocalPartialShapes = staticCompositionLocalOf { PartialShapes() }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.partialShapes: PartialShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalPartialShapes.current