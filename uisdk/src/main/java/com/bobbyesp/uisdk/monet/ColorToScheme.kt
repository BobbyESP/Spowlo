package com.bobbyesp.uisdk.monet

import androidx.annotation.ColorInt
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.bobbyesp.uisdk.monet.google.scheme.Scheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ColorToScheme {
  suspend fun convert(@ColorInt color: Int, dark: Boolean) = withContext(Dispatchers.Default) {
    val scheme = if (dark) { Scheme.dark(color) } else { Scheme.light(color) }

    ColorScheme(
      primary = scheme.primary.color(),
      onPrimary = scheme.onPrimary.color(),
      primaryContainer = scheme.primaryContainer.color(),
      onPrimaryContainer = scheme.onPrimaryContainer.color(),
      inversePrimary = scheme.inversePrimary.color(),
      secondary = scheme.secondary.color(),
      onSecondary = scheme.onSecondary.color(),
      secondaryContainer = scheme.secondaryContainer.color(),
      onSecondaryContainer = scheme.onSecondaryContainer.color(),
      tertiary = scheme.tertiary.color(),
      onTertiary = scheme.onTertiary.color(),
      tertiaryContainer = scheme.tertiaryContainer.color(),
      onTertiaryContainer = scheme.onTertiaryContainer.color(),
      background = scheme.background.color(),
      onBackground = scheme.onBackground.color(),
      surface = scheme.surface.color(),
      onSurface = scheme.onSurface.color(),
      surfaceVariant = scheme.surfaceVariant.color(),
      onSurfaceVariant = scheme.onSurfaceVariant.color(),
      surfaceTint = scheme.primary.color(), // defaults to primary, source: https://github.com/flutter/flutter/pull/100153
      inverseSurface = scheme.inverseSurface.color(),
      inverseOnSurface = scheme.inverseOnSurface.color(),
      error = scheme.error.color(),
      onError = scheme.onError.color(),
      errorContainer = scheme.errorContainer.color(),
      onErrorContainer = scheme.onErrorContainer.color(),
      outline = scheme.outline.color(),
      outlineVariant = scheme.outlineVariant.color(),
      scrim = scheme.scrim.color(),
    )
  }
}

private fun Int.color() = Color(this)