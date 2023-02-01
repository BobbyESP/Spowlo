package com.bobbyesp.spowlo.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bobbyesp.spowlo.ui.common.LocalDarkTheme

@Composable
infix fun Color.withNight(nightColor: Color): Color {
    return if (LocalDarkTheme.current.isDarkTheme()) nightColor else this
}

const val DEFAULT_SEED_COLOR = 0xFF415f76.toInt()