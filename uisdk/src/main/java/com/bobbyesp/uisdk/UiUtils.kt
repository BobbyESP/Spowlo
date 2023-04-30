package com.bobbyesp.uisdk

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

object UiUtils {
    @Composable
    fun localAsset(@DrawableRes id: Int) = ImageVector.vectorResource(id = id)
}