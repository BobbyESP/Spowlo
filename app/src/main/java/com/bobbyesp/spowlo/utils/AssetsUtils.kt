package com.bobbyesp.spowlo.utils

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

object AssetsUtils {
    @Composable
    fun LocalAsset(@DrawableRes id: Int) = ImageVector.vectorResource(id = id)
}