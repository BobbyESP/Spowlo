package com.bobbyesp.spowlo.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.bobbyesp.spowlo.presentation.ui.theme.ColorScheme.colorSchemeFromColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}

@Composable
fun Color.harmonizeWithPrimary(): Color {
    return Color(
        MaterialColors.harmonize(
            this.toArgb(),
            MaterialTheme.colorScheme.primary.toArgb()
        )
    )
}

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

@Composable
fun SpowloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrastModeEnabled: Boolean = false,
    seedColor: Int,
    isDynamicColorEnabled: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        DynamicColors.isDynamicColorAvailable() && isDynamicColorEnabled -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        else -> colorSchemeFromColor(seedColor, darkTheme)
    }.run {
        if (isHighContrastModeEnabled && darkTheme) copy(
            surface = Color.Black,
            background = Color.Black,
        )
        else this
    }
    val window = LocalView.current.context.findWindow()
    val view = LocalView.current

    window?.let { WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = darkTheme }

    rememberSystemUiController(window).setSystemBarsColor(Color.Transparent, !darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}