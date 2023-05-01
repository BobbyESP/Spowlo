package com.bobbyesp.uisdk

import androidx.annotation.DrawableRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.google.android.material.color.MaterialColors

object UiUtils {
    @Composable
    fun localAsset(@DrawableRes id: Int) = ImageVector.vectorResource(id = id)

    fun Color.applyOpacity(enabled: Boolean): Color {
        return if (enabled) this else this.copy(alpha = 0.62f)
    }

    @Composable
    fun Color.harmonizeWith(other: Color) =
        Color(MaterialColors.harmonize(this.toArgb(), other.toArgb()))

    @Composable
    fun Color.harmonizeWithPrimary(): Color =
        this.harmonizeWith(other = MaterialTheme.colorScheme.primary)

}