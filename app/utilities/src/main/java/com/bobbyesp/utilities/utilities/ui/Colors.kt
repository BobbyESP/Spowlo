package com.bobbyesp.utilities.utilities.ui

import androidx.compose.ui.graphics.Color

const val DEFAULT_SEED_COLOR = 0xFF415f76.toInt()

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}

fun Color.applyAlpha(alpha: Float): Color = this.copy(alpha = alpha)